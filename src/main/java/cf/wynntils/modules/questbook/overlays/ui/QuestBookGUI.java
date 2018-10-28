/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.overlays.ui;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.questbook.enums.QuestBookPage;
import cf.wynntils.modules.questbook.enums.QuestStatus;
import cf.wynntils.modules.questbook.instances.QuestInfo;
import cf.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuestBookGUI extends GuiScreen {

    boolean requestOpening = true;
    final ScreenRenderer render = new ScreenRenderer();

    long lastTick = 0;

    public QuestBookGUI() { }

    public void open() {
        lastTick = getMinecraft().world.getTotalWorldTime();
        acceptBack = false;
        acceptNext = false;
        animationCompleted = false;
        searchBarText = "";
        requestOpening = true;

        getMinecraft().displayGuiScreen(this);
    }

    //cache
    QuestBookPage page = QuestBookPage.DEFAULT;
    int currentPage = 1;
    boolean acceptNext = false;
    boolean acceptBack = false;
    QuestInfo overQuest = null;

    boolean animationCompleted = false;
    int selected = 0;


    //search bar
    String searchBarText = "";
    long text_flicker = System.currentTimeMillis();
    boolean keepForTime = false;

    //quest search
    ArrayList<QuestInfo> toSearch;

    //colors
    private static final CustomColor background_1 = CustomColor.fromString("000000", 0.3f);
    private static final CustomColor background_2 = CustomColor.fromString("000000", 0.2f);
    private static final CustomColor background_3 = CustomColor.fromString("00ff00", 0.3f);
    private static final CustomColor background_4 = CustomColor.fromString("008f00", 0.2f);

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT || keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL) return;
        if (keyCode == Keyboard.KEY_BACK) {
            if(searchBarText.length() <= 0) {
                return;
            }
            searchBarText = searchBarText.substring(0, searchBarText.length() - 1);
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
            text_flicker = System.currentTimeMillis();
            keepForTime = false;
        } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            searchBarText = searchBarText + typedChar;
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
            text_flicker = System.currentTimeMillis();
            keepForTime = true;
        }

        //updating questbook search
        if(page == QuestBookPage.QUESTS) {
            ArrayList<QuestInfo> quests = new ArrayList<>(QuestManager.getCurrentQuestsData());
            toSearch = !searchBarText.isEmpty() ? (ArrayList<QuestInfo>)quests.stream().filter(c -> c.getName().startsWith(searchBarText)).collect(Collectors.toList()) : quests;
            overQuest = null; currentPage = 1;
        }

        super.keyTyped(typedChar, keyCode);
    }

    long delay = System.currentTimeMillis();
    public void handleMouseInput() throws IOException {
        int mDwehll = Mouse.getEventDWheel();

        if(mDwehll >= 1 && (System.currentTimeMillis() - delay >= 100)) {
            if(acceptNext) {
                delay = System.currentTimeMillis();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
            }
        }else if(mDwehll <= -1 && (System.currentTimeMillis() - delay >= 100)) {
            if(acceptBack) {
                delay = System.currentTimeMillis();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
            }
        }

        super.handleMouseInput();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(overQuest != null) {
            if(QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(overQuest.getName())) {
                QuestManager.setTrackedQuest(null);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1f));
                return;
            }
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
            QuestManager.setTrackedQuest(overQuest);
            return;
        }
        ScaledResolution res = new ScaledResolution(getMinecraft());

        int posX = ((res.getScaledWidth()/2) - mouseX); int posY = ((res.getScaledHeight()/2) - mouseY);

        if(page == QuestBookPage.QUESTS) {
            if(acceptNext && posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
                return;
            }
            if(acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScreenRenderer.beginGL(0,0);

        int x = width / 2;
        int y = height / 2;

        if(requestOpening) {
            float animationTick = ((getMinecraft().world.getTotalWorldTime() - lastTick) + partialTicks) * 0.5f;

            if(animationTick <= 1) {
                render.scale(animationTick);

                x = (int)(x / animationTick);
                y = (int)(y / animationTick);
            }else{ render.resetScale(); requestOpening = false; }

        }else{
            x = width / 2; y = height / 2;
        }

        render.drawRect(Textures.UIs.quest_book, x-(339/2), y-(220/2), 0, 0, 339, 220);

        int posX = (x - mouseX); int posY = (y - mouseY);

        page = QuestBookPage.QUESTS;

        List<String> hoveredText = new ArrayList<>();

        //page per page
        //quests
        if(page == QuestBookPage.QUESTS) {
            render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);
            render.drawRect(Textures.UIs.quest_book, x+13, y-109, 52, 255, 133, 23);

            render.drawString("Here you can see all quests", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("available for you. You can", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("also search for a specific", x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("quest just by typing its name.", x - 154, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can also go to the next page", x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("by clicking on the two buttons", x - 154, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("or scrolling with your mouse.", x - 154, y + 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can pin/unpin a quest", x - 154, y + 50, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("by cliking on it.", x - 154, y + 60, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if(posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                hoveredText = Arrays.asList("Back to Menu", "§aAt Development");
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            }else{
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
            }

            render.drawRect(Textures.UIs.quest_book, x - 86, y - 100, 206, 252, 15, 15);
            if(posX >= 72 && posX <= 86 && posY >= 85 & posY <= 100) {
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    hoveredText = QuestManager.secretdiscoveryLore;
                } else { hoveredText = new ArrayList<>(QuestManager.discoveryLore); hoveredText.add(" "); hoveredText.add("§aHold shift to see Secret Discoveries!"); }
            }

            //searchBar
            if(searchBarText.length() <= 0) {
                render.drawString("Type to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }else{

                String text = searchBarText;

                if(render.getStringWidth(text) >= 110) {
                    int remove = searchBarText.length();
                    while(render.getStringWidth((text = searchBarText.substring(searchBarText.length() - remove))) >= 110) {
                        remove-=1;
                    }
                }

                if(System.currentTimeMillis() - text_flicker >= 500) {
                    keepForTime = !keepForTime;
                        text_flicker = System.currentTimeMillis();
                }

                if(keepForTime) {
                    render.drawString(text + "_", x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                }else{
                    render.drawString(text, x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                }
            }

            if(searchBarText.isEmpty()) toSearch = (ArrayList<QuestInfo>)QuestManager.getCurrentQuestsData().clone();

            int pages = toSearch.size() <= 13 ? 1 : (int)Math.ceil(toSearch.size() / 13d);
            if(pages < currentPage) {
                currentPage = pages;
            }

            //but next and back button
            if(currentPage == pages) {
                render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                acceptNext = false;
            }else{
                acceptNext = true;
                if(posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                }else {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                }
            }

            if(currentPage == 1) {
                acceptBack = false;
                render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
            }else{
                acceptBack = true;
                if(posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                }else {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                }
            }

            //calculating pages
            int v = (int)render.getStringWidth(currentPage + " / " + pages);
            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            //drawing all quests
            int currentY = 12;
            if(toSearch.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if(toSearch.size() <= i) { break; }

                    QuestInfo selected = toSearch.get(i);
                    if(!searchBarText.equals("") && !selected.getName().startsWith(searchBarText)) continue;

                    List<String> lore = new ArrayList<>(selected.getLore());

                    if(posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !requestOpening) {
                        if(lastTick == 0 && !animationCompleted) {
                            lastTick = getMinecraft().world.getTotalWorldTime();
                        }

                        this.selected = i;

                        int animationTick;
                        if(!animationCompleted) {
                            animationTick = (int)((getMinecraft().world.getTotalWorldTime() - lastTick) + partialTicks) * 30;
                            if(animationTick >= 133) {
                                animationCompleted = true;
                                animationTick = 133;
                            }
                        }else{ animationTick = 133; }

                        if(QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                            render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                            render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }else{
                            render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                            render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }

                        if(selected.getStatus() != QuestStatus.COMPLETED)  overQuest = selected;
                        hoveredText = lore;
                        GlStateManager.disableLighting();
                    }else{
                        if(this.selected == i) {
                            animationCompleted = false;

                            if(!requestOpening) lastTick = 0;
                            overQuest = null;
                        }

                        if(QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                            render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }else{
                            render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }
                    }

                    render.color(1, 1, 1, 1);
                    if (selected.getStatus() == QuestStatus.COMPLETED) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 223, 245, 11, 7);
                        lore.remove(lore.size() - 1);
                        lore.remove(lore.size() - 1);
                        lore.remove(lore.size() - 1);
                    }else if(selected.getStatus() == QuestStatus.CANNOT_START) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 235, 245, 7, 7);
                        lore.remove(lore.size() - 1);
                        lore.remove(lore.size() - 1);
                    } else if (selected.getStatus() == QuestStatus.CAN_START) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 254, 245, 11, 7);
                        if(QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                            lore.set(lore.size() - 1, "§c§lLeft click to unpin it!");
                        }else{ lore.set(lore.size() - 1, "§a§lLeft click to pin it!"); }
                    } else if (selected.getStatus() == QuestStatus.STARTED) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 245, 245, 8, 7);
                        if(QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                            lore.set(lore.size() - 1, "§c§lLeft click to unpin it!");
                        }else{ lore.set(lore.size() - 1, "§a§lLeft click to pin it!"); }
                    }

                    render.drawString(selected.getName(), x + 26, y -95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    currentY += 13;
                }
            }

            render.scale(2f);
            render.drawString("Quests", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.resetScale();
        }else if(page == QuestBookPage.CONFIGS) {
            render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);

            //book
            if(posX >= 109 && posX <= 143 && posY >= -28 && posY <= 0) {
                render.drawRect(Textures.UIs.quest_book, x-140, y, 0, 249, 31, 27);
            }else { render.drawRect(Textures.UIs.quest_book, x - 140, y, 0, 221, 31, 27); }
            render.drawRect(Textures.UIs.quest_book, x-50, y, 280, 248, 27, 27);

            render.scale(2f);
            render.drawString("Configs", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.resetScale();
        }else if(page == QuestBookPage.DEFAULT) {
            render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);

            //book
            if(posX >= 109 && posX <= 143 && posY >= -28 && posY <= 0) {
                render.drawRect(Textures.UIs.quest_book, x-140, y, 0, 249, 31, 27);
            }else { render.drawRect(Textures.UIs.quest_book, x - 140, y, 0, 221, 31, 27); }

            //engine
            if(posX >= 24 && posX <= 50 && posY >= -28 && posY <= 0) {
                render.drawRect(Textures.UIs.quest_book, x-50, y, 280, 248, 27, 27);
                hoveredText = Arrays.asList("In Development");
            }else{ render.drawRect(Textures.UIs.quest_book, x-50, y, 280, 221, 27, 27); }

            render.drawString("Please select an option", x-79, y-30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString("^ Quests", x-110, y+35, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString("Configs ^", x-55, y+35, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            render.scale(2f);
            render.drawString("Wynntils v" + Reference.VERSION, (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.resetScale();
        }

        //default texts
        render.scale(0.7f);
        render.drawString("v" + Reference.VERSION, (x - 80) / 0.7f, (y + 86) / 0.7f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        render.resetScale();

        GlStateManager.disableLighting();
        if(hoveredText != null) drawHoveringText(hoveredText, mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
        ScreenRenderer.endGL();
    }

    public Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

}
