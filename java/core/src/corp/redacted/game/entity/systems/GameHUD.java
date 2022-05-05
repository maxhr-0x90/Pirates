package corp.redacted.game.entity.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import corp.redacted.game.loader.Assets;
import corp.redacted.game.views.MainScreen;

public class GameHUD extends Stage {
    private BitmapFont font;
    private Table table;

    private Label timerLabel;
    private ProgressBar lifeBatG, lifeBatD;
    private Image deadG, deadD;
    private Skin skinLife;

    public GameHUD(){
        super(new FitViewport(2560, 1440));
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Assets.pirateFont));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 168;
        fontParameter.borderWidth = 5;
        fontParameter.borderColor = Color.DARK_GRAY;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        table = new Table();
        setupTable();
        addActor(table);
    }

    /**
     * Place les éléments sur l'écran
     */
    private void setupTable(){
        table.setFillParent(true);
        //table.setDebug(true);
        table.top();

        skinLife = new Skin(Gdx.files.internal("skins/life/life.json"));

        timerLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        lifeBatG = new ProgressBar(0, 100, 1, true, skinLife);
        lifeBatD = new ProgressBar(0, 100, 1, true, skinLife);
        deadG = new Image(skinLife, "skull");
        deadD = new Image(skinLife, "skull");

        table.add();
        table.add(timerLabel).expandX();
        table.row();
        table.add(lifeBatG).pad(20).height(500);
        table.add();
        table.add(lifeBatD).pad(20).height(500);
        table.row();
        table.add(deadG);
        table.add();
        table.add(deadD);
    }

    /**
     * Crée un rendu du HUD
     */
    public void render(float delta){
        updateTimer();

        act(delta);
        draw();
    }

    /**
     * Met à jour les 2 barres de vies
     *
     * @param lifeG Points de vie pour la barre de vie gauche
     * @param lifeD Points de vie pour la barre de vie droite
     */
    public void updateLife(int lifeG, int lifeD){
        setLife(lifeBatG, deadG, lifeG);
        setLife(lifeBatD, deadD, lifeD);
    }

    /**
     * Met à jour une barre de vie
     *
     * @param life Barre de vie
     * @param icon Icon de mort
     * @param val Points de vie
     */
    private void setLife(ProgressBar life, Image icon, int val){
        life.setValue(val);

        if (val <= 0){
            icon.setDrawable(skinLife, "skull_act");
            life.setDisabled(true);
        }
    }

    /**
     * Met à jour le timer
     */
    private void updateTimer(){
        float timer = Math.max(MainScreen.timer, 0);
        String time = String.format("%d:%02d", (int) Math.floor(timer / 60), (int) timer % 60);
        timerLabel.setText(time);
    }

    /**
     * Permet de redimensionner le HUD
     *
     * @param width largeur du HUD
     * @param height hauteur du HUD
     */
    public void resize(int width, int height){
        getViewport().update(width, height, true);
    }

    /**
     * Réinitialise le HUD
     */
    public void reset(){
        deadG.setDrawable(skinLife, "skull");
        deadD.setDrawable(skinLife, "skull");
        lifeBatG.setDisabled(false);
        lifeBatD.setDisabled(false);
    }
}
