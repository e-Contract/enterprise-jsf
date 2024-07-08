package be.e_contract.jsf;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("directController")
@ApplicationScoped
public class DirectController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectController.class);

    private Map<String, Set<Session>> groups;
    private ReentrantReadWriteLock readWriteLock;
    private boolean block;

    @PostConstruct
    public void postConstruct() {
        this.groups = new ConcurrentHashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public void addSession(Session session) {
        Lock lock = this.readWriteLock.writeLock();
        try {
            lock.lock();
            Map<String, String> pathParameters = session.getPathParameters();
            String group = pathParameters.get("group");
            if (null == group) {
                return;
            }
            Set<Session> sessions = this.groups.get(group);
            if (null == sessions) {
                sessions = new HashSet<>();
                this.groups.put(group, sessions);
            }
            sessions.add(session);
            sendMessage(group, "new member " + session.getId());
        } finally {
            lock.unlock();
        }
    }

    public Set<String> getGroups() {
        Lock lock = this.readWriteLock.readLock();
        try {
            lock.lock();
            return this.groups.keySet();
        } finally {
            lock.unlock();
        }
    }

    public void sendMessage(String group) {
        sendMessage(group, " message " + new Date());
    }

    public void sendMessage(String group, String message) {
        Lock lock = this.readWriteLock.readLock();
        try {
            lock.lock();
            Set<Session> sessions = this.groups.get(group);
            if (null == sessions) {
                return;
            }
            for (Session session : sessions) {
                if (!session.isOpen()) {
                    continue;
                }
                RemoteEndpoint.Async async = session.getAsyncRemote();
                async.sendText(group + ": " + message);
            }
        } finally {
            lock.unlock();
        }
    }

    public void sendPing(String group) {
        Lock lock = this.readWriteLock.readLock();
        try {
            lock.lock();
            Set<Session> sessions = this.groups.get(group);
            if (null == sessions) {
                return;
            }
            ByteBuffer buffer = ByteBuffer.wrap("ping".getBytes());
            for (Session session : sessions) {
                if (!session.isOpen()) {
                    continue;
                }
                RemoteEndpoint.Async async = session.getAsyncRemote();
                try {
                    async.sendPing(buffer);
                } catch (IOException | IllegalArgumentException ex) {
                    LOGGER.error("ping error: " + ex.getMessage());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void pingAll() {
        Lock lock = this.readWriteLock.readLock();
        try {
            lock.lock();
            if (this.groups.isEmpty()) {
                return;
            }
            ByteBuffer buffer = ByteBuffer.wrap("ping".getBytes());
            for (Set<Session> sessions : this.groups.values()) {
                for (Session session : sessions) {
                    if (!session.isOpen()) {
                        continue;
                    }
                    RemoteEndpoint.Async async = session.getAsyncRemote();
                    try {
                        async.sendPing(buffer);
                    } catch (IOException | IllegalArgumentException ex) {
                        LOGGER.error("ping error: " + ex.getMessage());
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeSession(Session session) {
        Lock lock = this.readWriteLock.writeLock();
        try {
            lock.lock();
            Map<String, String> pathParameters = session.getPathParameters();
            String group = pathParameters.get("group");
            if (null == group) {
                return;
            }
            Set<Session> sessions = this.groups.get(group);
            if (null == sessions) {
                return;
            }
            boolean removed = sessions.remove(session);
            if (sessions.isEmpty()) {
                this.groups.remove(group);
            }
            if (removed) {
                sendMessage(group, "member removed " + session.getId());
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isBlock() {
        return this.block;
    }

    public void setBlock(boolean block) {
        LOGGER.debug("blocked: {}", block);
        this.block = block;
    }
}
