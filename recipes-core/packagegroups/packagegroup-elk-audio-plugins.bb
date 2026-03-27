SUMMARY = "Package group for third-party open-source audio plugins"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit packagegroup

RDEPENDS:${PN} = "\
    airwindows \
    cloudreverb \
    dexed \
    dragonfly-reverb \
    gxplugins \
    infamousplugins \
    irconvolution \
    jc303 \
    juicysf \
    ob-xf \
    rsalgoverb \
    shiro \
    x42 \
"
