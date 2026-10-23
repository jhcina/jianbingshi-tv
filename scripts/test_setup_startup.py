import unittest
from setup_startup import plan

class StartupSetupTests(unittest.TestCase):
    def test_exact_mftr0_profile(self):
        self.assertEqual(plan("MiTV-MFTR0","OS3.0.101.0.UFTMATV","io.github.jhcina.yitv",{}),{"start_3rd_app":"io.github.jhcina.yitv","boot_auto_play":"0"})
    def test_unknown_firmware_does_not_get_xiaomi_settings(self):
        with self.assertRaises(ValueError):plan("MiTV-MFTR0","future","io.github.jhcina.yitv",{})
    def test_other_brand_is_refused(self):
        with self.assertRaises(ValueError):plan("TCL","OS3.0.101.0.UFTMATV","io.github.jhcina.yitv",{})
    def test_existing_other_target_needs_explicit_replace(self):
        with self.assertRaises(ValueError):plan("MiTV-MFTR0","OS3.0.101.0.UFTMATV","io.github.jhcina.yitv",{"start_3rd_app":"com.example.other"})
    def test_own_target_is_idempotent(self):
        self.assertEqual(plan("MiTV-MFTR0","OS3.0.101.0.UFTMATV","io.github.jhcina.yitv",{"start_3rd_app":"io.github.jhcina.yitv"})["start_3rd_app"],"io.github.jhcina.yitv")
    def test_unknown_package_is_refused(self):
        with self.assertRaises(ValueError):plan("MiTV-MFTR0","OS3.0.101.0.UFTMATV","com.example.app",{})
    def test_private_package_does_not_point_to_public_edition(self):
        self.assertEqual(plan("MiTV-MFTR0","OS3.0.101.0.UFTMATV","io.github.op7418.guizangtv.yi",{})["start_3rd_app"],"io.github.op7418.guizangtv.yi")
