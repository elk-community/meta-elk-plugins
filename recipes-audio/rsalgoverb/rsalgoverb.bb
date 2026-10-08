SUMMARY = "RS Algorithmic Verb — algorithmic reverb VST3 plugin"
HOMEPAGE = "https://github.com/elk-community/RSAlgorithmicVerb"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

# gitsm: required for JUCE submodule (top-level JUCE/).
# find_package(JUCE CONFIG REQUIRED) resolves JUCE from the staging sysroot,
# so the submodule is fetched but not used during the build.
SRC_URI = "gitsm://github.com/elk-community/RSAlgorithmicVerb;protocol=https;nobranch=1"
SRCREV = "20c04d805b56a92eb3c8df965fedac3ecb2d0731"
PV = "0.5.5+git"
inherit cmake audio-plugin-common pkgconfig

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

# X11 libs are build-time only: juce_gui_basics unconditionally pulls in X headers
# (JUCE_GUI_BASICS_INCLUDE_XHEADERS=1) regardless of DISTRO_FEATURES.
DEPENDS = " \
    juce \
    zlib \
    alsa-lib \
    freetype \
    fontconfig \
    libx11 \
    libxext \
    libxinerama \
    libxrandr \
    libxcursor \
"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DCOPY_PLUGIN_AFTER_BUILD=OFF \
"

# VST3 bundle: RSAlgorithmicVerb_artefacts/Release/VST3/RSAlgorithmicVerb.vst3/
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst3
    cp -r ${B}/RSAlgorithmicVerb_artefacts/Release/VST3/RSAlgorithmicVerb.vst3 \
          ${D}${PLUGIN_INSTALL_DIR}/vst3/
}

PACKAGES =+ "${PN_VST3}"
FILES:${PN_VST3} += "${PLUGIN_INSTALL_DIR}/vst3"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN_VST3}"

INSANE_SKIP:${PN_VST3} += "already-stripped"
