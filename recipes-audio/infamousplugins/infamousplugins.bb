SUMMARY = "Infamous Plugins — LV2 audio effect and synth plugin collection"
HOMEPAGE = "https://github.com/ssj71/infamousPlugins"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit audio-plugin-common cmake

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

DEPENDS = "lv2 pkgconfig-native"

SRC_URI = "git://github.com/ssj71/infamousPlugins;protocol=https;nobranch=1"
SRCREV = "8c0e11ecac624e014caba00e5cf4fb0ca2faa806"
PV = "0.0+git${SRCPV}"
S = "${WORKDIR}/git"

# PLUGINS is a CMake CACHE STRING; overriding via EXTRA_OECMAKE is the idiomatic
# CMake approach — no patch needed. ewham and lushlife are excluded from the build.
#
# Semicolons must be escaped as \; because bitbake inlines EXTRA_OECMAKE as a
# literal into the generated run.do_configure shell script (it does NOT keep it
# as a shell variable reference). The shell then treats \; as an escaped
# semicolon (not a command separator) and cmake receives the raw ; list.
#
# CMAKE_INSTALL_PREFIX and LIBDIR redirect the install to /opt/plugins/lv2/ to
# match the layer-wide convention (overriding cmake.bbclass default of /usr).
# LIBDIR=. collapses the prefix/lib/lv2 path to prefix/lv2.
EXTRA_OECMAKE += "\
    -DPLUGINS=casynth\;envfollower\;hip2b\;powerup\;powercut\;cheapdist\;stuck\;bentdelay\;mindi\;octolo \
    -DCMAKE_INSTALL_PREFIX=${PLUGIN_INSTALL_DIR} \
    -DLIBDIR=. \
"

# LV2 plugin bundles + infamous-rule utility binary
FILES:${PN} += "${PLUGIN_INSTALL_DIR}/lv2 ${PLUGIN_INSTALL_DIR}/bin"
