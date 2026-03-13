IMAGE_INSTALL:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'include-third-party-plugins', 'packagegroup-elk-audio-plugins', '', d)}"
