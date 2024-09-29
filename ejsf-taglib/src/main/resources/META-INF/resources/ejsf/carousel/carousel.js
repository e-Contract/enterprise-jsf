/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

class EJSFCarousel {

    element;
    imageData;
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

    get zoomDialog() {
        return this.element.querySelector(".ejsf-carousel-zoom-dialog");
    }

    get zoomDialogImage() {
        return this.zoomDialog.querySelector("img:first-of-type");
    }

    get imageCaption() {
        return this.element.querySelector(".ejsf-carousel-image-caption");
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

    zoom() {
        this.cancelAutoPlay();
        if (this.activeImageIndex >= this.imageData.length) {
            return;
        }
        let zoomImage = this.imageData[this.activeImageIndex].zoomImage;
        if (!zoomImage) {
            zoomImage = this.imageData[this.activeImageIndex].image;
        }
        this.zoomDialogImage.src = zoomImage;
    }

    changeActiveImage(idx) {
        if (idx < 0) {
            return;
        }
        if (idx >= this.thumbnailsElement.childElementCount) {
            return;
        }
        if (this.activeImageIndex === idx) {
            return;
        }
        this.activeThumbnailImg.style.removeProperty("opacity");
        this.activeImageIndex = idx;
        this.activeThumbnailImg.style.opacity = 1;
        this.image.src = this.imageData[idx].image;

        if (this.imageData.length === 1) {
            this.prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            this.prevNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
            this.nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            this.nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
        } else if (idx === 0) {
            this.prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            this.prevNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
            this.nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            this.nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
        } else if (idx === this.imageData.length - 1) {
            this.nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-disabled");
            this.nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-enabled");
            this.prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            this.nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
        } else {
            this.prevNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            this.prevNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
            this.nextNavElement.classList.add("ejsf-carousel-thumbnails-nav-enabled");
            this.nextNavElement.classList.remove("ejsf-carousel-thumbnails-nav-disabled");
        }

        let count = this.imageData.length;
        let width = this.thumbnailsWindowElement.getBoundingClientRect().width;
        if (width > count * (80 + 10)) {
            return;
        }
        let translation = -idx * (80 + 10) + (width - (80 + 10)) * (idx + 1) / count;
        this.thumbnailsElement.style.transform = "translate(" + translation + "px, 0px)";
    }

    updateImageCaption() {
        let caption = this.imageData[this.activeImageIndex].caption;
        if (caption) {
            this.imageCaption.textContent = caption;
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
        this.image.addEventListener("click", () => {
            $this.zoom();
        });
        this.image.addEventListener("load", () => {
            $this.updateImageCaption();
        });
        this.zoomDialogImage.addEventListener("click", () => {
            $this.zoomDialog.close();
        });
        this.zoomDialogImage.addEventListener("load", () => {
            $this.zoomDialog.showModal();
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
    }
}
