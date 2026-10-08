SUMMARY = "OB-Xf virtual analog synthesizer (VST3 plugin)"
HOMEPAGE = "https://github.com/elk-community/OB-Xf"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

# gitsm: required for submodule tree (sst-*, fmt, simde, clap-juce-extensions,
# melatonin_inspector, MTS-ESP). libs/JUCE is present but not used;
# find_package(JUCE CONFIG REQUIRED) resolves it from the staging sysroot.
SRC_URI = " \
    gitsm://github.com/elk-community/OB-Xf;protocol=https;nobranch=1 \
    file://0001-Add-OBXF_BUILD_CLAP-option-to-disable-CLAP-format.patch \
"
SRCREV = "17101d5216b8a610ea767aa4e395e66eb3b2ce1f"
PV = "0.0+git"
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
    -DOBXF_BUILD_CLAP=OFF \
"

# sst-plugininfra's version_from_versionfile_or_git() tries git rev-parse as a
# fallback, which fails in the Yocto sandbox. Pre-write BUILD_VERSION with SRCREV.
do_configure:prepend() {
    printf 'SST_VERSION_INFO\n%.8s\n-no-tag-\nmain\n%s\n' \
        "${SRCREV}" "${PV}" \
        > "${S}/BUILD_VERSION"
}

# VST3 bundle: OB-Xf_artefacts/Release/VST3/OB-Xf.vst3/Contents/aarch64-linux/OB-Xf.so
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst3
    cp -r ${B}/OB-Xf_artefacts/Release/VST3/OB-Xf.vst3 \
          ${D}${PLUGIN_INSTALL_DIR}/vst3/
}

PACKAGES =+ "${PN_VST3}"
FILES:${PN_VST3} += "${PLUGIN_INSTALL_DIR}/vst3"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "${PN_VST3}"

INSANE_SKIP:${PN_VST3} += "already-stripped"
