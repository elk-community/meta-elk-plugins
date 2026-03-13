SUMMARY = "SHIRO plugins: Shiroverb reverb, Modulay modulated delay, Larynx vocoder, Harmless synthesiser"
HOMEPAGE = "https://github.com/elk-community/SHIRO-Plugins"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

inherit audio-plugin-common

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

# dpf/ is a git submodule (DISTRHO Plugin Framework)
DEPENDS = "pkgconfig-native"

SRC_URI = "gitsm://github.com/elk-community/SHIRO-Plugins;protocol=https;nobranch=1"
SRCREV = "7850099cec1153cc40b4c007e21723437a77aecf"
PV = "0.0+git${SRCPV}"
S = "${WORKDIR}/git"

# Only build the VST target per plugin — the only format confirmed to work on Elk Pi
# (per README-ELK.md). The DPF build system detects the cross-toolchain via
# 'CC -dumpmachine' and sets CPU/OS flags accordingly; Yocto's CC/CXX/CFLAGS/CXXFLAGS
# flow through correctly without any patching.
do_compile() {
    for plugin in shiroverb modulay larynx harmless; do
        cd ${S}/plugins/${plugin}
        make vst PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
        cd ${S}
    done
}

# DPF places all output binaries in ${S}/bin/
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst
    find ${S}/bin -maxdepth 1 -name "*-vst.so" -type f \
        -exec install -m 0755 {} ${D}${PLUGIN_INSTALL_DIR}/vst/ \;
}

FILES:${PN} += "${PLUGIN_INSTALL_DIR}/vst"

# DPF links with -Wl,--strip-all, so symbols are stripped before Yocto's do_package
INSANE_SKIP:${PN} += "already-stripped"
