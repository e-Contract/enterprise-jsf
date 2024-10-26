/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

class EJSFCarousel {

    element;
    imageData;
    lightbox;
    activeImageIndex = 0;
    active = false;
    lazyFirst = false;

    constructor(element, imageData) {
        this.element = element;
        this.imageData = imageData;
    }

    get prevNavElement() {
        return this.element.querySelector(".ejsf-carousel-thumbnails-prev");
    }

    get nextNavElement() {
        return this.element.querySelector(".ejsf-carousel-thumbnails-next");
    }

    get thumbnailsElement() {
        return this.element.querySelector(".ejsf-carousel-thumbnails-content");
    }

    get thumbnailsWindowElement() {
        return this.element.querySelector(".ejsf-carousel-thumbnails-window");
    }

    get activeThumbnailImg() {
        return this.thumbnailsElement.querySelector("img[data-image-index='" + this.activeImageIndex + "']");
    }

    get image() {
        return this.element.querySelector(".ejsf-carousel-image");
    }

    get imageCaption() {
        return this.element.querySelector(".ejsf-carousel-image-caption");
    }

    get imageCaptionContent() {
        return this.element.querySelector(".ejsf-carousel-image-caption-content");
    }

    get imageCaptionTitle() {
        return this.element.querySelector(".ejsf-carousel-image-caption-title");
    }

    prevNavClicked() {
        this.cancelAutoPlay();
        let newImageIndex = this.activeImageIndex - 1;
        this.changeActiveImage(newImageIndex);
    }

    nextNavClicked() {
        this.cancelAutoPlay();
        this.changeActiveImage(this.activeImageIndex + 1);
    }

    imageClicked() {
        this.cancelAutoPlay();
        if (this.activeImageIndex >= this.imageData.length) {
            return;
        }
        let onclickLocation = this.imageData[this.activeImageIndex].onclickLocation;
        if (onclickLocation) {
            window.location.assign(onclickLocation);
            return;
        }
        if (!this.lightbox) {
            let $this = this;
            this.lightbox = new PhotoSwipeLightbox({
                pswpModule: PhotoSwipe
            });
            this.lightbox.addFilter("numItems", (numItems) => {
                return $this.imageData.length;
            });
            this.lightbox.addFilter("itemData", (itemData, index) => {
                let zoomImage = $this.imageData[index].zoomImage;
                if (!zoomImage) {
                    zoomImage = $this.imageData[index].image;
                }
                return {
                    src: zoomImage
                };
            });
            this.lightbox.init();
        }
        this.lightbox.loadAndOpen(this.activeImageIndex);
    }

    changeActiveImage(idx) {
        if (idx < 0) {
            return;
        }
        let thumbnailsElement = this.thumbnailsElement;
        if (idx >= thumbnailsElement.childElementCount) {
            return;
        }
        if (this.activeImageIndex === idx) {
            return;
        }
        this.activeThumbnailImg.style.removeProperty("opacity");
        this.activeImageIndex = idx;
        this.activeThumbnailImg.style.opacity = 1;
        this.image.src = this.imageData[idx].image;

        let prevNavElement = this.prevNavElement;
        let nextNavElement = this.nextNavElement;
        if (this.imageData.length === 1) {
            prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            prevNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
            nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
        } else if (idx === 0) {
            prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            prevNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
            nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
        } else if (idx === this.imageData.length - 1) {
            nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
            prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            prevNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
        } else {
            prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            prevNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
            nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
        }

        let width = this.thumbnailsWindowElement.getBoundingClientRect().width;
        let totalWidth = 0;
        for (let cidx = 0; cidx < thumbnailsElement.childElementCount; cidx++) {
            totalWidth += thumbnailsElement.children[cidx].offsetWidth;
            totalWidth += 10; // padding
        }
        let translation;
        if (width >= totalWidth) {
            translation = 0;
        } else {
            let offset = 0;
            // idx - 1 to keep previous thumbnail visible
            for (let cidx = 0; cidx < idx - 1; cidx++) {
                offset += thumbnailsElement.children[cidx].offsetWidth;
                offset += 10; // padding
            }
            let maxOffset = totalWidth - width;
            if (offset > maxOffset) {
                offset = maxOffset;
            }
            translation = -offset;
        }
        thumbnailsElement.style.transform = "translate(" + translation + "px, 0px)";
    }

    updateImageCaption() {
        let caption = this.imageData[this.activeImageIndex].caption;
        let captionTitle = this.imageData[this.activeImageIndex].captionTitle;
        if (caption || captionTitle) {
            if (caption) {
                this.imageCaptionContent.textContent = caption;
            } else {
                this.imageCaptionContent.textContent = "";
            }
            if (captionTitle) {
                this.imageCaptionTitle.textContent = captionTitle;
            } else {
                this.imageCaptionTitle.textContent = "";
            }
            this.imageCaption.style.visibility = "visible";
        } else {
            this.imageCaption.style.visibility = "hidden";
        }
    }

    initEventListeners() {
        let $this = this;
        this.prevNavElement.addEventListener("click", () => {
            $this.prevNavClicked();
        });
        this.nextNavElement.addEventListener("click", () => {
            $this.nextNavClicked();
        });
        let image = this.image;
        image.addEventListener("click", () => {
            $this.imageClicked();
        });
        image.addEventListener("load", () => {
            $this.updateImageCaption();
        });
    }

    initThumbnail(idx) {
        if (null === this.imageData) {
            return;
        }
        if (this.imageData.length <= idx) {
            return;
        }
        let imgElement = document.createElement("img");
        imgElement.classList.add("ejsf-carousel-thumbnails-image");
        imgElement.setAttribute("data-image-index", idx);
        if (idx === this.activeImageIndex) {
            imgElement.style.opacity = 1.0;
        }
        let $this = this;
        imgElement.addEventListener("load", () => {
            imgElement.addEventListener("click", () => {
                $this.cancelAutoPlay();
                $this.changeActiveImage(idx);
            });
            $this.thumbnailsElement.appendChild(imgElement);
            setTimeout(() => {
                // run on next event cycle
                $this.initThumbnail(idx + 1);
            });
        });
        imgElement.src = this.imageData[idx].thumbnail;
    }

    initThumbnails() {
        this.activeImageIndex = 0;
        setTimeout(() => {
            // run on next event cycle
            this.initThumbnail(0);
        });
    }

    initLazyFirst() {
        if (!this.lazyFirst) {
            return;
        }
        if (null === this.imageData) {
            return;
        }
        if (0 === this.imageData.length) {
            return;
        }
        setTimeout(() => {
            // run on next event cycle
            this.image.src = this.imageData[0].image;
        });
    }

    init(lazyFirst = false) {
        this.lazyFirst = lazyFirst;
        let $this = this;
        this.intersectionObserver = new IntersectionObserver(function (entries) {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    if ($this.active) {
                        if ($this.autoPlayDelay) {
                            $this.autoPlay($this.autoPlayDelay);
                        }
                    } else {
                        $this.active = true;
                        $this.initEventListeners();
                        $this.initThumbnails();
                        if ($this.autoPlayDelay) {
                            $this.autoPlay($this.autoPlayDelay);
                        }
                        $this.initLazyFirst();
                    }
                } else {
                    $this.cancelAutoPlay();
                }
            });
        });
        this.intersectionObserver.observe(this.element);
    }

    doAutoPlay() {
        let nextIndex = this.activeImageIndex + 1;
        if (nextIndex === this.thumbnailsElement.childElementCount) {
            nextIndex = 0;
        }
        this.changeActiveImage(nextIndex);
    }

    cancelAutoPlay() {
        if (this.timer) {
            window.clearInterval(this.timer);
            this.timer = null;
        }
    }

    autoPlay(delay) {
        if (delay <= 0) {
            this.cancelAutoPlay();
            this.autoPlayDelay = null;
            return;
        }
        this.autoPlayDelay = delay;
        if (!this.active) {
            return;
        }
        this.cancelAutoPlay();
        let $this = this;
        this.timer = window.setInterval(() => {
            $this.doAutoPlay();
        }, delay);
    }

    destroy() {
        this.cancelAutoPlay();
        if (this.intersectionObserver) {
            this.intersectionObserver.disconnect();
            this.intersectionObserver = null;
        }
        if (this.lightbox) {
            this.lightbox.destroy();
            this.lightbox = null;
        }
    }
}
