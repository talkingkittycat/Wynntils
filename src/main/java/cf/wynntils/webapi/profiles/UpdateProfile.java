package cf.wynntils.webapi.profiles;

import cf.wynntils.Reference;
import cf.wynntils.modules.core.overlays.UpdateOverlay;
import cf.wynntils.webapi.WebReader;

public class UpdateProfile {

    boolean hasUpdate = false;
    String latestUpdate = Reference.VERSION;

    private WebReader versions;

    public UpdateProfile() {
        new Thread(() -> {
            try{

                versions = new WebReader("http://api.wynntils.cf/versions");

                try{
                    Integer latest = Integer.valueOf(versions.get(Reference.MINECRAFT_VERSIONS).replace(".", "").replace("\n", ""));
                    Integer actual = Integer.valueOf(latestUpdate.replace(".", ""));

                    if(latest > actual) {
                        System.out.println("Update Found");
                        UpdateOverlay.reset();
                        hasUpdate = true;
                        latestUpdate = versions.get(Reference.MINECRAFT_VERSIONS);
                    }

                }catch (Exception ignored) { ignored.printStackTrace(); }

            }catch(Exception ignored) { ignored.printStackTrace(); }
        }).start();
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public void forceUpdate() {
        hasUpdate = true;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

}
