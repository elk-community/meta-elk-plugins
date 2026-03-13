# meta-elk-plugins

OpenEmbedded/Yocto layer containing recipes for third-party open-source audio
plugins packaged for Elk Audio OS.

## Layer details

| Property | Value |
|----------|-------|
| Priority | 9 |
| Yocto release | Scarthgap |
| Depends on | `core`, `meta-elk` (for JUCE, FluidSynth, LV2) |

## Available plugins

| Recipe | Description | Format | License |
|--------|-------------|--------|---------|
| `airwindows` | Large collection of audio effect plugins | VST2 | MIT |
| `dexed` | DX7-style FM synthesizer | VST3 | GPL-3.0 |
| `dragonfly-reverb` | Hall-style reverb based on freeverb3 | VST | GPL-2.0 |
| `gxplugins` | GuitarX guitar effect collection | LV2 | GPL-3.0 |
| `irconvolution` | IR convolution effect | VST3 | Proprietary |
| `juicysf` | SoundFont (SF2) player | VST3 | GPL-3.0 |
| `ob-xf` | OB-Xf virtual analog synthesizer | VST3 | GPL-3.0 |
| `shiro` | Shiroverb, Modulay, Larynx, Harmless | VST | GPL-3.0 |
| `x42` | Fat1 auto-tune, TestSignal, Tuna tuner | LV2 | GPL-2.0 |

Plugins install to `/opt/plugins/<format>/` on the target.

## Adding a plugin to an image

Include a plugin in `packagegroup-elk-audio-plugins` or add it directly to
`IMAGE_INSTALL` in your image recipe or `local.conf`:

```
IMAGE_INSTALL:append = " dexed-vst3 juicysf-vst3"
```

Each JUCE-based VST3 plugin ships as a `<name>-vst3` package containing the
`.vst3` bundle. The base `<name>` package is empty.

## Notes

**VST2 (airwindows)** requires a Steinberg VST2.4 SDK. Set `VST2SDK_PATH` in
`local.conf` pointing to the SDK root. See `extra/enable-vst2.yml` in
kas-configs.

**juicysf** has a runtime dependency on `fluidsynth` (declared via
`RDEPENDS`), which must also be present on the target rootfs.

---
Copyright 2017-2026 Elk Audio AB, Stockholm, Sweden.
