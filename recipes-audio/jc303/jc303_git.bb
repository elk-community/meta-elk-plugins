SUMMARY = "JC303 Roland TB-303 bass synthesizer emulation (VST3 plugin)"
HOMEPAGE = "https://github.com/elk-community/jc303"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=903d0fc86333f132c49874fae0bc8c62"

# No .gitmodules: all FetchContent deps are pre-populated via SRC_URI destsuffix
# to match FETCHCONTENT_BASE_DIR=${S}/lib, then FETCHCONTENT_FULLY_DISCONNECTED=ON
# disables network access during configure.
#
# Dep tree:
#   jc303/CMakeLists.txt        → clap-juce-extensions (gitsm: has clap submodules)
#                               → chowdsp_utils
#   guitarml-byod/CMakeLists.txt → rtneural (gitsm: has xsimd submodule)
#                                → math_approx
#                                → ea_variant
SRC_URI = " \
    git://github.com/elk-community/jc303;protocol=https;nobranch=1;name=jc303 \
    gitsm://github.com/free-audio/clap-juce-extensions.git;protocol=https;nobranch=1;name=clap;destsuffix=git/lib/juce-clap-extensions-src \
    git://github.com/Chowdhury-DSP/chowdsp_utils.git;protocol=https;nobranch=1;name=chowdsp;destsuffix=git/lib/chowdsp_utils-src \
    gitsm://github.com/jatinchowdhury18/RTNeural.git;protocol=https;nobranch=1;name=rtneural;destsuffix=git/lib/rtneural-src \
    git://github.com/Chowdhury-DSP/math_approx.git;protocol=https;nobranch=1;name=mathapprox;destsuffix=git/lib/math_approx-src \
    git://github.com/eyalamirmusic/Variant.git;protocol=https;nobranch=1;name=eavariant;destsuffix=git/lib/ea_variant-src \
"
SRCREV_jc303 = "9f3456ee729edbcbc1a2913089ea0a834758c658"
SRCREV_clap = "e1f67893cc409a40c1154fa2e78c97046da24ce0"
SRCREV_chowdsp = "ffc70ba399f9afaeefb996eb14e55a1d487270b8"
SRCREV_rtneural = "04cb333bc4b174760958a77c7ce076eae38fe8e4"
SRCREV_mathapprox = "0c68d4d17242d707ba07fa7f1901692b7ed72d58"
SRCREV_eavariant = "3fce49cfca50ba3b05026d41ffc4911a8e653378"
SRCREV_FORMAT = "jc303"

PV = "0.12.3+git${SRCPV}"
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
    -DFETCHCONTENT_FULLY_DISCONNECTED=ON \
"

# VST3 bundle: JC303_artefacts/Release/VST3/JC303.vst3/Contents/aarch64-linux/JC303.so
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst3
    cp -r ${B}/JC303_artefacts/Release/VST3/JC303.vst3 \
          ${D}${PLUGIN_INSTALL_DIR}/vst3/
}

PACKAGES =+ "${PN_VST3}"
FILES:${PN_VST3} += "${PLUGIN_INSTALL_DIR}/vst3"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN_VST3}"

INSANE_SKIP:${PN_VST3} += "already-stripped"
