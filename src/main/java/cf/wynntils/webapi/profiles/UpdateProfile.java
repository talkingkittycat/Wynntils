package cf.wynntils.webapi.profiles;

import cf.wynntils.Reference;
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
                    Integer latest = Integer.valueOf(versions.get("Mod").replace(".", "").replace("\n", ""));
                    Integer actual = Integer.valueOf(latestUpdate.replace(".", ""));

                    if(latest > actual) {
                        hasUpdate = true;
                        latestUpdate = versions.get("Mod");
                    }

                }catch (Exception ignored) { ignored.printStackTrace(); }

            }catch(Exception ignored) { ignored.printStackTrace(); }
        }).start();
    }

    public boolean modHasUpdate() {
        return hasUpdate;
    }

    public String getModLatestUpdate() {
        return latestUpdate;
    }

}
