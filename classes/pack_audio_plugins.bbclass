# Auto package audio plugins

inherit audio-plugin-common

PACKAGES =+ "${PN_LV2} ${PN_LADSPA} ${PN_DSSI} ${PN_VST} ${PN_VST3}"

FILES:${PN_LV2}    += "${PLUGIN_INSTALL_DIR}/lv2"
FILES:${PN_LADSPA} += "${PLUGIN_INSTALL_DIR}/ladspa"
FILES:${PN_DSSI}   += "${PLUGIN_INSTALL_DIR}/dssi"
FILES:${PN_VST}    += "${PLUGIN_INSTALL_DIR}/vst"
FILES:${PN_VST3}   += "${PLUGIN_INSTALL_DIR}/vst3"
