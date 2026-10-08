SUMMARY = "Dexed FM synthesizer (VST3 plugin)"
HOMEPAGE = "https://github.com/elk-community/dexed"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

# gitsm: required for submodules (clap-juce-extensions, tuning-library,
# surgesynthteam_tuningui, MTS-ESP). libs/JUCE and libs/vst3sdk are present
# but not used; find_package(JUCE CONFIG REQUIRED) resolves from the staging sysroot.
SRC_URI = " \
    gitsm://github.com/elk-community/dexed;protocol=https;nobranch=1 \
"
SRCREV = "4bc6bb017976d3f0cd6290d8c0cef071962f9639"
PV = "1.0.1+git"
inherit cmake audio-plugin-common pkgconfig

# Explicit assignment ensures the variable is in the datastore for FILES expansion.
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

# Source/CMakeLists.txt is a subdirectory target, so the VST3 bundle lands under Source/:
# Source/Dexed_artefacts/Release/VST3/Dexed.vst3/Contents/aarch64-linux/Dexed.so
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst3
    cp -r ${B}/Source/Dexed_artefacts/Release/VST3/Dexed.vst3 \
          ${D}${PLUGIN_INSTALL_DIR}/vst3/
}

PACKAGES =+ "${PN_VST3}"
FILES:${PN_VST3} += "${PLUGIN_INSTALL_DIR}/vst3"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN_VST3}"

INSANE_SKIP:${PN_VST3} += "already-stripped"
