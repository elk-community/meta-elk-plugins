SUMMARY = "x42 LV2 plugins: Fat1 auto-tune, TestSignal generator, Tuna chromatic tuner"
HOMEPAGE = "https://github.com/elk-community"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "\
    file://fat1/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://testsignal/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://tuna/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"

inherit audio-plugin-common pkgconfig

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

# fat1 and tuna have a robtk git submodule; testsignal does not
DEPENDS = "lv2 fftw"

SRC_URI = "\
    gitsm://github.com/elk-community/fat1.lv2;protocol=https;nobranch=1;name=fat1;destsuffix=fat1 \
    gitsm://github.com/elk-community/tuna.lv2;protocol=https;nobranch=1;name=tuna;destsuffix=tuna \
    git://github.com/elk-community/testsignal.lv2;protocol=https;nobranch=1;name=testsignal;destsuffix=testsignal \
"

SRCREV_fat1      = "725b8d19ed0b4337790e7d57e3dbf61af3e077b0"
SRCREV_tuna      = "014a69826959a69a3d90af1de85291334637b859"
SRCREV_testsignal = "4426df0ab2a5653d09ca9fd8a08966fe247ec900"
SRCREV_FORMAT    = "fat1_tuna_testsignal"

PV = "0.0+git"

# All three repos are siblings in UNPACKDIR; no single canonical S directory
S = "${UNPACKDIR}"

do_compile() {
    for plugin in fat1 tuna testsignal; do
        cd ${UNPACKDIR}/${plugin}
        make MOD=TRUE PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
        cd ${UNPACKDIR}
    done
}

do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/lv2
    for plugin in fat1 tuna testsignal; do
        cd ${UNPACKDIR}/${plugin}
        make install MOD=TRUE \
            DESTDIR=${D} \
            LV2DIR=${PLUGIN_INSTALL_DIR}/lv2 \
            PKG_CONFIG="${STAGING_BINDIR_NATIVE}/pkg-config"
        cd ${UNPACKDIR}
    done
}

FILES:${PN} += "${PLUGIN_INSTALL_DIR}/lv2"

# The plugin Makefiles strip .so files before Yocto's do_package can do so
INSANE_SKIP:${PN} += "already-stripped"
