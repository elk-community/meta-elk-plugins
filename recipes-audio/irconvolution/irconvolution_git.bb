SUMMARY = "IR Convolution audio plugin (VST3)"
HOMEPAGE = "https://github.com/elk-community/irconvolution"
# No LICENSE file in repository; copyright held by Elk Audio AB.
LICENSE = "CLOSED"

# gitsm: required for elk-plugin-extensions submodule (elk_vst3_extensions interface lib).
# simde is also a submodule but is not referenced by the build.
SRC_URI = " \
    gitsm://github.com/elk-community/irconvolution;protocol=https;nobranch=1 \
"
SRCREV = "f86db60f7fae62b0147bff22ee4057a72251e1fb"
PV = "0.0.1+git${SRCPV}"
S = "${WORKDIR}/git"

inherit cmake audio-plugin-common

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
    pkgconfig-native \
"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DCOPY_PLUGIN_AFTER_BUILD=OFF \
"

# PRODUCT_NAME "IR Convolution" (with space) determines the bundle filename.
# VST3 bundle: IRConvolution_artefacts/Release/VST3/IR Convolution.vst3/Contents/aarch64-linux/IRConvolution.so
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst3
    cp -r "${B}/IRConvolution_artefacts/Release/VST3/IR Convolution.vst3" \
          ${D}${PLUGIN_INSTALL_DIR}/vst3/
}

PACKAGES =+ "${PN_VST3}"
FILES:${PN_VST3} += "${PLUGIN_INSTALL_DIR}/vst3"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN_VST3}"

INSANE_SKIP:${PN_VST3} += "already-stripped"
