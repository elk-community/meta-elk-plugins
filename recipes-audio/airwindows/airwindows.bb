SUMMARY = "Airwindows - collection of open-source VST2 audio effect plugins"
HOMEPAGE = "https://github.com/elk-community/airwindows"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8cf6137b1c7e405c24d3e7e6ee2ae0b3"

inherit audio-plugin-common cmake

# Explicit assignment ensures the variable is in the datastore for FILES expansion
PLUGIN_INSTALL_DIR = "/opt/plugins"

# VST2SDK_PATH must be set in local.conf pointing to the Steinberg VST2.4 SDK root
# (the directory that contains public.sdk/ and pluginterfaces/).
# See extra/enable-vst2.yml.
VST2SDK_PATH ?= ""

SRC_URI = "git://github.com/elk-community/airwindows;protocol=https;nobranch=1"
SRCREV = "038696743ae8f62da01e90297d6b129cba47bcf0"
PV = "0.0+git"
# CMakeLists.txt lives under plugins/LinuxVST/
OECMAKE_SOURCEPATH = "${S}/plugins/LinuxVST"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"

do_configure:prepend() {
    if [ -z "${VST2SDK_PATH}" ]; then
        bbfatal "VST2SDK_PATH is not set. Configure it in local.conf (see extra/enable-vst2.yml)."
    fi

    # CMakeLists.txt hardcodes VSTSDK_ROOT to plugins/LinuxVST/include/vstsdk and
    # expects a flat layout there: the three .cpp SDK sources plus headers, with a
    # pluginterfaces/ subtree alongside for the transitive include
    # "pluginterfaces/vst2.x/aeffectx.h". We build that structure via symlinks into
    # the real Steinberg VST2.4 SDK tree.
    VSTSDK_SRC="${VST2SDK_PATH}/public.sdk/source/vst2.x"
    VSTSDK_DIR="${S}/plugins/LinuxVST/include/vstsdk"

    install -d "${VSTSDK_DIR}"

    for f in audioeffect.cpp audioeffectx.cpp vstplugmain.cpp \
              audioeffect.h audioeffectx.h aeffeditor.h; do
        ln -sf "${VSTSDK_SRC}/$f" "${VSTSDK_DIR}/$f"
    done

    # pluginterfaces/ is needed for the "#include pluginterfaces/vst2.x/aeffectx.h"
    # path in audioeffectx.h, resolved relative to VSTSDK_ROOT.
    ln -sf "${VST2SDK_PATH}/pluginterfaces" "${VSTSDK_DIR}/pluginterfaces"

    # add_subdirectory(include/vstsdk) in CMakeLists.txt needs a CMakeLists.txt stub
    touch "${VSTSDK_DIR}/CMakeLists.txt"
}

do_install() {
    install -d ${D}${PLUGIN_INSTALL_DIR}/vst
    find ${B} -name "*.so" -type f \
        -exec install -m 0755 {} ${D}${PLUGIN_INSTALL_DIR}/vst/ \;
}

FILES:${PN} += "${PLUGIN_INSTALL_DIR}/vst"

# cmake Release builds strip symbols; suppress the QA warning
INSANE_SKIP:${PN} += "already-stripped"

# The VST2 SDK sources are symlinked in from VST2SDK_PATH (outside ${S}),
# so their DWARF paths keep the build directory. Only the -dbg package
# is affected.
INSANE_SKIP:${PN}-dbg += "buildpaths"
