SUMMARY = "Dragonfly Reverb - hall-style reverb VST plugin based on freeverb3"
HOMEPAGE = "https://github.com/elk-community/dragonfly-reverb"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit audio-plugin-common pkgconfig

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

# dpf/ is a git submodule (DISTRHO Plugin Framework); freeverb3/ is inlined in the repo
DEPENDS = ""

SRC_URI = "gitsm://github.com/elk-community/dragonfly-reverb;protocol=https;nobranch=1"
SRCREV = "a91c5122c46a0f6fec5f838ad063b25ad391973a"
PV = "0.0+git"
# This DPF version unconditionally errors when DGL (OpenGL+X11) is not found,
# even though the headless VST target has no dependency on it. Patch the error
# out so the build proceeds with HAVE_DGL empty (headless VST only).
do_configure:prepend() {
    sed -i '/^\$(error DGL missing/d' ${S}/dpf/Makefile.base.mk
}

# Build freeverb3 and the VST plugin without going through the top-level Makefile
# (which also includes dpf/Makefile.base.mk and would re-trigger the DGL check).
do_compile() {
    # NOOPT=true replaces the hardcoded x86 '-msse -msse2' BASE_OPTS with
    # arch-neutral '-O2 -fdata-sections -ffunction-sections'; Yocto's CFLAGS
    # (with correct aarch64 tuning) are appended via BUILD_C/CXX_FLAGS anyway
    make -C ${S}/freeverb3 NOOPT=true PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
    make -C ${S}/plugins/dragonfly-reverb vst NOOPT=true PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
}

# DPF places output in ${S}/bin/
do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst
    find ${S}/bin -maxdepth 1 -name "*-vst.so" -type f \
        -exec install -m 0755 {} ${D}${PLUGIN_INSTALL_DIR}/vst/ \;
}

FILES:${PN} += "${PLUGIN_INSTALL_DIR}/vst"

# DPF links with -Wl,--strip-all, so symbols are stripped before Yocto's do_package
INSANE_SKIP:${PN} += "already-stripped"
