SUMMARY = "Dragonfly Reverb - hall-style reverb VST plugin based on freeverb3"
HOMEPAGE = "https://github.com/elk-community/dragonfly-reverb"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464"

inherit audio-plugin-common pkgconfig

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

# dpf/ is a git submodule (DISTRHO Plugin Framework); freeverb3/ is inlined in the repo
DEPENDS = ""

SRC_URI = "gitsm://github.com/elk-community/dragonfly-reverb;protocol=https;nobranch=1"
SRCREV = "440ec7b3b3db2fec34b9f80cf4890102ca14d1c3"
PV = "0.0+git"
# This DPF version unconditionally errors when DGL (OpenGL+X11) is not found,
# even though the headless VST target has no dependency on it. Patch the error
# out so the build proceeds with HAVE_DGL empty (headless VST only).
do_compile() {
    # NOOPT=true replaces the hardcoded x86 '-msse -msse2' BASE_OPTS with
    # arch-neutral '-O2 -fdata-sections -ffunction-sections'; Yocto's CFLAGS
    # (with correct aarch64 tuning) are appended via BUILD_C/CXX_FLAGS anyway.
    # freeverb3 is now inlined in common/freeverb and built with each plugin.
    make -C ${S}/dpf/dgl USE_FILE_BROWSER=false NOOPT=true PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
    for plugin in hall room plate; do
        make -C ${S}/plugins/dragonfly-${plugin}-reverb vst2 USE_FILE_BROWSER=false NOOPT=true PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
    done
    make -C ${S}/plugins/dragonfly-early-reflections vst2 USE_FILE_BROWSER=false NOOPT=true PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
}

do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst
    find ${S}/bin -maxdepth 1 -name "*-vst.so" -type f \
        -exec install -m 0755 {} ${D}${PLUGIN_INSTALL_DIR}/vst/ \;
}

FILES:${PN} += "${PLUGIN_INSTALL_DIR}/vst"

# DPF links with -Wl,--strip-all, so symbols are stripped before Yocto's do_package
INSANE_SKIP:${PN} += "already-stripped"
