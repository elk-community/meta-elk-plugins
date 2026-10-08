SUMMARY = "JuicySF soundfont player (VST3 plugin)"
HOMEPAGE = "https://github.com/elk-community/juicysfplugin"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

# gitsm: required for elk-plugin-extensions submodule (elk_vst3_extensions interface lib).
SRC_URI = " \
    gitsm://github.com/elk-community/juicysfplugin;protocol=https;nobranch=1 \
"
SRCREV = "e508cb08a6f67c9d8b3ea7bf602111c5f4654c26"
PV = "3.0.0+git"
inherit cmake audio-plugin-common

# Explicit assignment ensures the variable is in the datastore for FILES expansion.
PLUGIN_INSTALL_DIR = "/opt/plugins"

# X11 libs are build-time only: juce_gui_basics unconditionally pulls in X headers
# (JUCE_GUI_BASICS_INCLUDE_XHEADERS=1) regardless of DISTRO_FEATURES.
DEPENDS = " \
    juce \
    fluidsynth \
    zlib \
    alsa-lib \
    freetype \
    fontconfig \
    libx11 \
    libxext \
    libxinerama \
    libxrandr \
    libxcursor \
    pkgconfig-native \
"

# BUILD_SHARED_LIBS=ON: link against libfluidsynth.so rather than the full
# static dep closure (glib, gthread, …).
EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DCOPY_PLUGIN_AFTER_BUILD=OFF \
    -DBUILD_SHARED_LIBS=ON \
"

# VST3 bundle: JuicySFPlugin_artefacts/Release/VST3/juicysfplugin.vst3/Contents/aarch64-linux/JuicySFPlugin.so
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst3
    cp -r ${B}/JuicySFPlugin_artefacts/Release/VST3/juicysfplugin.vst3 \
          ${D}${PLUGIN_INSTALL_DIR}/vst3/
}

PACKAGES =+ "${PN_VST3}"
FILES:${PN_VST3} += "${PLUGIN_INSTALL_DIR}/vst3"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN_VST3}"

RDEPENDS:${PN_VST3} = "fluidsynth"

INSANE_SKIP:${PN_VST3} += "already-stripped"
