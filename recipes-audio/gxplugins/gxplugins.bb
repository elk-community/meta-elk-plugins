SUMMARY = "GxPlugins - GuitarX LV2 plugin collection"
HOMEPAGE = "https://github.com/elk-community/GxPlugins.lv2"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

inherit audio-plugin-common pkgconfig

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

DEPENDS = "lv2"

SRC_URI = "gitsm://github.com/elk-community/GxPlugins.lv2;protocol=https;nobranch=1"
SRCREV = "fb8864a4b069c429122a2918fdbcbfbf00260b57"
PV = "0.0+git"
# build-elk.sh hardcodes ARM32 OPT_FLAGS which break aarch64 builds. Instead,
# we replicate its Makefile-patching logic here and call make directly so that
# Yocto's CFLAGS/CXXFLAGS (correctly tuned for the target) are used as-is.
do_compile() {
    mkdir -p ${S}/build-elk

    for plugin_dir in ${S}/Gx*.lv2; do
        [ -d "$plugin_dir" ] || continue
        cd "$plugin_dir"
        [ -f "Makefile" ] || { cd ${S}; continue; }

        # Remove the CPU optimisation block (same sed logic as build-elk.sh)
        start=$(grep -n "check CPU and supported optimization flags" Makefile | head -1 | cut -d: -f1)
        end=$(grep -n "set bundle name" Makefile | head -1 | cut -d: -f1)
        if [ -n "$start" ] && [ -n "$end" ]; then
            end=$(expr $end - 2)
            sed -i "${start},${end}d" Makefile
        fi

        make PKGCONFIG="${STAGING_BINDIR_NATIVE}/pkg-config" mod

        lv2_folder=$(find . -maxdepth 1 -name "*.lv2" -type d | head -n 1)
        if [ -n "$lv2_folder" ]; then
            mv "$lv2_folder" ${S}/build-elk/
        fi

        cd ${S}
    done
}

do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/lv2
    for bundle in $(find ${S}/build-elk -maxdepth 1 -name "*.lv2" -type d); do
        cp -r $bundle ${D}${PLUGIN_INSTALL_DIR}/lv2/
    done
}

FILES:${PN} += "${PLUGIN_INSTALL_DIR}/lv2"

# The plugin Makefiles strip .so files before Yocto's do_package can do so
INSANE_SKIP:${PN} += "already-stripped"
