/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.Utils;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;

@Named("announcementController")
@ViewScoped
public class AnnouncementController implements Serializable {

    private String message;

    private Integer version;

    private List<Announcement> announcements;

    public static class Announcement implements Serializable {

        private final int version;
        private final String message;

        public Announcement(int version, String message) {
            this.version = version;
            this.message = message;
        }

        public int getVersion() {
            return this.version;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public List<Announcement> getAnnouncements() {
        return this.announcements;
    }

    public String getAnnouncement(int version) {
        for (Announcement announcement : this.announcements) {
            if (announcement.getVersion() == version) {
                return announcement.getMessage();
            }
        }
        return null;
    }

    @PostConstruct
    public void postConstruct() {
        this.announcements = new LinkedList<>();
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void addAnnouncement() {
        if (StringUtils.isEmpty(this.message)) {
            Utils.invalidateInput("message");
            return;
        }
        if (null == this.version) {
            this.version = 1;
        } else {
            this.version++;
        }
        Announcement announcement = new Announcement(this.version, this.message);
        this.announcements.add(announcement);
        this.message = null;
    }

    public void removeAnnouncement(Announcement announcement) {
        this.announcements.remove(announcement);
    }

    public String getLatestAnnouncement() {
        if (this.announcements.isEmpty()) {
            return null;
        }
        return this.announcements.get(this.announcements.size() - 1).getMessage();
    }
}
