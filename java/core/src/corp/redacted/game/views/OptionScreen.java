package corp.redacted.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sun.tools.javac.util.StringUtils;
import corp.redacted.game.Game;
import corp.redacted.game.loader.Assets;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.files.FileHandle;


/**
 * Écran présent au démarrage du jeu pour le choix des options de jeu
 */
public class OptionScreen implements Screen {
    private final Game PARENT;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;

    private Stage stage;
    private Table table;
    private Skin skin;

    private Label loading;

    private boolean ok;

    public OptionScreen(Game parent){
        PARENT = parent;
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Assets.pirateFont));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 64;
        fontParameter.borderWidth = 5;
        fontParameter.borderColor = Color.DARK_GRAY;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        stage = new Stage(new FitViewport(2560, 1440));
        table = new Table();
        skin = new Skin(Gdx.files.internal("skins/skinperso.json"));

        setupTable();

        stage.addActor(table);
    }

    /**
     * Place les éléments sur l'écran
     */
    private void setupTable(){
        table.setFillParent(true);

        Label invit = new Label("Bienvenue Pirates !", new Label.LabelStyle(font, Color.WHITE));
        loading = new Label("Option de jeu", new Label.LabelStyle(font, Color.WHITE));

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Assets.pirateFont));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 40;
        fontParameter.borderWidth = 5;
        fontParameter.borderColor = Color.DARK_GRAY;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        Label lab = new Label("", new Label.LabelStyle(font, Color.WHITE));
        Label lab2 = new Label("Premiere moitie d'equipe", new Label.LabelStyle(font, Color.WHITE));
        Label lab3 = new Label("Deuxieme moitie d'equipe", new Label.LabelStyle(font, Color.WHITE));

        /* CheckBox */

        Label cb1L = new Label("Rame droite",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb1 = new CheckBox(null, skin);
        cb1.setChecked(true);
        ok = true;
        cb1.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("1");
          }
        });
        Label cb1LB = new Label("Rame droite",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb1B = new CheckBox(null, skin);
        cb1B.setChecked(true);
        ok = true;
        cb1B.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("1");
          }
        });

        Label cb2L = new Label("Rame gauche",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb2 = new CheckBox(null, skin);
        cb2.setChecked(true);
        cb2.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("2");
          }
        });
        Label cb2LB = new Label("Rame gauche",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb2B = new CheckBox(null, skin);
        cb2B.setChecked(true);
        cb2B.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("2");
          }
        });

        Label cb3L = new Label("Tir droite",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb3 = new CheckBox(null, skin);
        cb3.setChecked(true);
        cb3.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("3");
          }
        });
        Label cb3LB = new Label("Tir droite",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb3B = new CheckBox(null, skin);
        cb3B.setChecked(true);
        cb3B.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("3");
          }
        });

        Label cb4L = new Label("Tir gauche",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb4 = new CheckBox(null, skin);
        cb4.setChecked(true);
        cb4.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("4");
          }
        });
        Label cb4LB = new Label("Tir gauche",new Label.LabelStyle(font, Color.WHITE));
        CheckBox cb4B = new CheckBox(null, skin);
        cb4B.setChecked(true);
        cb4B.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
              ok = !ok;
              System.out.println("4");
          }
        });


        table.add(lab);
        table.add(invit).fillX().uniformX();
        table.row().pad(0, 0, 200, 0);

        table.add(lab);
        table.add(loading);
        table.row().pad(0,0,0,0);

        table.add(lab2);
        // table.add(lab);
        table.add(lab);
        table.add(lab3);
        table.add(lab);
        table.row().pad(0,0,0,0);

        table.add(cb1L);
        table.add(cb1);
        // table.add(lab);
        table.add(cb1LB);
        table.add(cb1B);
        table.row().pad(0,0,0,0);

        table.add(cb2L);
        table.add(cb2);
        // table.add(lab);
        table.add(cb2LB);
        table.add(cb2B);
        table.row().pad(0,0,0,0);

        table.add(cb3L);
        table.add(cb3);
        // table.add(lab);
        table.add(cb3LB);
        table.add(cb3B);
        table.row().pad(0,0,0,0);

        table.add(cb4L);
        table.add(cb4);
        // table.add(lab);
        table.add(cb4LB);
        table.add(cb4B);




    }



    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        checkCtrl();
    }

    /**
     * Vérifie si une commande a été entrée pour passer au jeu
     */
    private void checkCtrl(){
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.X)){
            PARENT.switchScreen(Game.START);
        }
    }

    @Override
    public void resize(int x, int y) {
        stage.getViewport().update(x, y, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
