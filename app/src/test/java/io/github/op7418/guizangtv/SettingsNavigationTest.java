package io.github.op7418.guizangtv;
import org.junit.Test;
import static org.junit.Assert.*;
public class SettingsNavigationTest {
    @Test public void startupWizardReturnsToTvSettings(){assertEquals("tv",SettingsNavigation.parent("startup"));}
    @Test public void visualChildrenReturnToAppearance(){for(String p:new String[]{"layout","type","focus"})assertEquals("appearance",SettingsNavigation.parent(p));}
    @Test public void photoChildrenStayInPhotos(){for(String p:new String[]{"wallpaper","private-wallpaper","rotation"})assertEquals("background",SettingsNavigation.parent(p));}
    @Test public void appEditingAndCityKeepTheirParent(){assertEquals("home",SettingsNavigation.parent("item:com.example.app"));assertEquals("home",SettingsNavigation.parent("add"));assertEquals("weather",SettingsNavigation.parent("city"));}
    @Test public void optionalFeaturesReturnToMore(){assertEquals("more",SettingsNavigation.parent("experiments"));assertEquals("more",SettingsNavigation.parent("weather"));}
    @Test public void onlyKnownSystemSettingsAreConsolidated(){assertTrue(SettingsNavigation.systemSettings("com.android.tv.settings"));assertTrue(SettingsNavigation.systemSettings("com.xiaomi.mitv.settings"));assertFalse(SettingsNavigation.systemSettings("com.example.settings"));assertFalse(SettingsNavigation.systemSettings("com.ktcp.video"));}
}
