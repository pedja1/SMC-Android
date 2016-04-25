package rs.pedjaapps.smc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.HashSet;
import java.util.Set;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.Collider;
import rs.pedjaapps.smc.object.DynamicObject;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.LevelRegion;
import rs.pedjaapps.smc.object.Sprite;
import rs.pedjaapps.smc.object.enemy.Enemy;
import rs.pedjaapps.smc.object.items.Coin;
import rs.pedjaapps.smc.utility.Constants;

public class LevelEditorScreen implements Screen, InputProcessor
{
    private static final float RESIZE_OFFSET = 0.09f;
    private static final float PANE_WIDTH = 200;
    private OrthographicCamera cam;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private SpriteBatch spriteBatch;

    private float width, height;

    private Array<GameObject> gameObjects;

    private Stage stage;
    private List<String> itemList;

    private GameObject draggingObject;
    private float downDistanceX, downDistanceY;

    private boolean dragging;

    private Set<Integer> pressedKeys = new HashSet<>(5);
    private boolean gridShowing;

    private FileHandle file;
    private TextureAtlas atlas;

    private boolean debug = true;

    private Rectangle tmp1 = new Rectangle();
    private Rectangle tmp2 = new Rectangle();

    private boolean verticalResizeHover, horizontalResizeHover;

    private ScrollPane scrollPane;
    private Window propertiesDialog;
    private SelectBox<Sprite.Type> typeSelectBox;
    private SelectBox<DynamicObject.Direction> directionSelectBox;
    private TextField pointsTextField;

    public LevelEditorScreen(Array<GameObject> gameObjects, FileHandle file)
    {
        this.gameObjects = gameObjects;
        if (this.gameObjects == null)
            this.gameObjects = new Array<>();
        this.file = file;
    }

    @Override
    public void show()
    {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        this.cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        this.cam.update();

        spriteBatch = new SpriteBatch();

        Skin skin = new Skin(Gdx.files.internal("data/editor/uiskin.json"), new TextureAtlas(Gdx.files.internal("data/editor/uiskin.atlas")));
        itemList = new List<>(skin);

        Assets.manager.load("data/assets.atlas", TextureAtlas.class);
        Assets.manager.finishLoading();
        atlas = Assets.manager.get("data/assets.atlas");

        for (GameObject go : gameObjects)
        {
            go.initAssets();
        }

        Array<String> items = new Array<>();
        for (TextureAtlas.AtlasRegion region : atlas.getRegions())
        {
            if (!region.name.startsWith("character/")
                    && !region.name.startsWith("environment/backgrounds/")
                    && !region.name.startsWith("environment/clouds/"))
            {
                if (!region.name.contains("coins/yellow") && !region.name.contains("coins/red")
                        || ((region.name.contains("coins/yellow") && region.name.endsWith("1")) || (region.name.contains("coins/red") && region.name.endsWith("1"))))
                {
                    String name = region.name;
                    if (name.startsWith("environment/"))
                        name = name.replaceAll("environment/", "");
                    items.add(name);
                }
            }
        }
        items.add("collider");

        itemList.setItems(items);
        stage = new Stage(new StretchViewport(width, height));


        scrollPane = new ScrollPane(itemList);
        scrollPane.setBounds(0, 0, PANE_WIDTH, height);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setPosition(0, 0);
        scrollPane.setTransform(true);
        stage.addActor(scrollPane);

        typeSelectBox = new SelectBox<>(skin);
        typeSelectBox.setItems(Sprite.Type.values());
        typeSelectBox.setSelected(Sprite.Type.passive);

        directionSelectBox = new SelectBox<>(skin);
        directionSelectBox.setItems(DynamicObject.Direction.values());
        directionSelectBox.setSelected(DynamicObject.Direction.left);

        pointsTextField = new TextField("", skin);
        pointsTextField.setMessageText("points");
        pointsTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        TextButton btnClose = new TextButton("X", skin);
        final Label typeLabel = new Label("Type:", skin);
        final Label directionLabel = new Label("Direction:", skin);

        Button save = new TextButton("Save", skin);
        save.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(draggingObject != null)
                {
                    if(draggingObject instanceof Enemy)
                    {
                        ((Enemy) draggingObject).setDirection(directionSelectBox.getSelected());
                    }
                    else if(draggingObject instanceof Coin)
                    {
                        ((Coin) draggingObject).points = Integer.parseInt(pointsTextField.getText());
                    }
                    else if(draggingObject instanceof Sprite)
                    {
                        ((Sprite) draggingObject).type = typeSelectBox.getSelected();
                    }
                }
                propertiesDialog.setVisible(false);
            }
        });

        directionSelectBox.setVisible(false);
        typeSelectBox.setVisible(false);
        pointsTextField.setVisible(false);

        propertiesDialog = new Window("Properties", skin);
        propertiesDialog.getTitleTable().add(btnClose).height(propertiesDialog.getPadTop());
        propertiesDialog.setPosition(300, 300);
        propertiesDialog.defaults().spaceBottom(10);
        propertiesDialog.row().fill().expandX();
        //window.add(iconButton);
        //window.add(buttonMulti);
        //window.add(imgButton);
        //window.add(imgToggleButton);
        //window.row();
        //window.add(checkBox);
        //window.add(slider).minWidth(100).fillX().colspan(3);
        //window.row();
        //window.add(typeLabel);
        propertiesDialog.add(typeSelectBox);
        propertiesDialog.row();
        //window.add(directionLabel);
        propertiesDialog.add(directionSelectBox).fillX();
        propertiesDialog.row();
        propertiesDialog.add(pointsTextField);
        propertiesDialog.row();
        propertiesDialog.add(save);
        //window.add(textfield).minWidth(100).expandX().fillX().colspan(3);
        //window.row();
        //window.add(splitPane).fill().expand().colspan(4).maxHeight(200);
        //window.row();
        //window.add(passwordLabel).colspan(2);
        //window.add(passwordTextField).minWidth(100).expandX().fillX().colspan(2);
        //window.row();
        //window.add(fpsLabel).colspan(4);
        propertiesDialog.pack();
        propertiesDialog.setVisible(false);

        btnClose.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                propertiesDialog.setVisible(false);
            }
        });

        stage.addActor(propertiesDialog);

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateObjects(delta);

        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawObjects();
        spriteBatch.end();

        drawDebug();

        stage.act(delta);
        stage.draw();

    }

    private void drawObjects()
    {
        for (int i = 0; i < gameObjects.size; i++)
        {
            gameObjects.get(i).render(spriteBatch);
        }
    }

    private void updateObjects(float delata)
    {
        for (int i = 0; i < gameObjects.size; i++)
        {
            gameObjects.get(i).update(delata);
        }
    }

    private void drawDebug()
    {
        // render blocks
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (gridShowing)
        {
            shapeRenderer.setColor(Color.GRAY);
            for (int i = 1; i < cam.viewportWidth; i++)
            {
                shapeRenderer.line(i, 0, i, cam.viewportHeight);
            }

            for (int i = 1; i < cam.viewportWidth; i++)
            {
                shapeRenderer.line(0, i, cam.viewportWidth, i);
            }
        }

        for (int i = 0; i < gameObjects.size; i++)
        {
            GameObject go = gameObjects.get(i);
            Rectangle drawRect = go.bounds;
            if (go != draggingObject && debug)
            {
                if(go instanceof Collider)
                {
                    shapeRenderer.setColor(0, 1, 0, 1);
                }
                else
                {
                    shapeRenderer.setColor(1, 0, 0, 1);
                }
                shapeRenderer.rect(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
            }
            else if (go == draggingObject)
            {
                shapeRenderer.setColor(0, 0, 1, 1);
                shapeRenderer.rect(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
            }
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;

        Constants.initCamera();

        cam = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.setToOrtho(false, Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
        cam.update();
    }

    @Override
    public void hide()
    {
    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
        //Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose()
    {

    }

    // * InputProcessor methods ***************************//

    @Override
    public boolean keyDown(int keycode)
    {
        pressedKeys.add(keycode);
        stage.keyDown(keycode);
        if (keycode == Input.Keys.A)
        {
            addObject();
        }
        else if (keycode == Input.Keys.FORWARD_DEL)
        {
            gameObjects.removeValue(draggingObject, false);
            draggingObject = null;
        }
        else if (keycode == Input.Keys.D)
        {
            debug = !debug;
        }
        else if (keycode == Input.Keys.G)
        {
            gridShowing = !gridShowing;
        }
        else if (pressedKeys.contains(Input.Keys.CONTROL_LEFT) && keycode == Input.Keys.S)
        {
            //save
            LevelRegion levelRegion = new LevelRegion();
            levelRegion.gameObjects = gameObjects;
            Json json = new Json();
            String jsonString = json.toJson(levelRegion);
            file.writeString(jsonString, false);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        pressedKeys.remove(keycode);
        stage.keyUp(keycode);

        if (keycode == Input.Keys.S && !pressedKeys.contains(Input.Keys.CONTROL_LEFT))
        {
            snapToGrid();
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
        stage.keyTyped(character);
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button)
    {
        if(stage.touchDown(x, y, pointer, button))
            return true;
        float myY = invertY(y) / (height / cam.viewportHeight);
        float myX = x / (width / cam.viewportWidth);

        boolean found = false;
        for (GameObject go : gameObjects)
        {
            Rectangle bounds;
            if (verticalResizeHover || horizontalResizeHover)
            {
                bounds = tmp1;
                bounds.set(go.bounds.x - RESIZE_OFFSET, go.bounds.y - RESIZE_OFFSET, go.bounds.width + RESIZE_OFFSET * 2, go.bounds.height + RESIZE_OFFSET * 2);
            }
            else
            {
                bounds = go.bounds;
            }
            if (bounds.contains(myX, myY))
            {
                found = true;
                draggingObject = go;
                downDistanceX = myX - go.bounds.x;
                downDistanceY = myY - go.bounds.y;
                break;
            }
        }
        if (!found)
        {
            draggingObject = null;
            downDistanceX = 0;
            downDistanceY = 0;
        }

        if(button == Input.Buttons.RIGHT && draggingObject != null)
        {
            propertiesDialog.setPosition(x, invertY(y));
            propertiesDialog.setVisible(true);

            directionSelectBox.setVisible(false);
            typeSelectBox.setVisible(false);
            pointsTextField.setVisible(false);

            if(draggingObject instanceof Enemy)
            {
                directionSelectBox.setVisible(true);
                directionSelectBox.setSelected(((Enemy) draggingObject).getDirection());
            }
            else if(draggingObject instanceof Coin)
            {
                pointsTextField.setText(Integer.toString(((Coin)draggingObject).points));
                pointsTextField.setVisible(true);
            }
            else if(draggingObject instanceof Sprite)
            {
                typeSelectBox.setSelected(((Sprite) draggingObject).type);
                typeSelectBox.setVisible(true);
            }
            else
            {
                propertiesDialog.setVisible(false);
            }
        }

        return true;
    }

    private void addObject()
    {
        String selected = itemList.getSelected();

        String textureName = "environment/" + selected;
        if (selected.startsWith("tiles"))
        {
            TextureRegion region = atlas.findRegion(textureName);
            Sprite sprite = new Sprite(3, 3, 1, (float) region.getRegionHeight() / (float) region.getRegionWidth());
            sprite.textureAtlas = Assets.DEFAULT_ATLAS;
            sprite.textureName = textureName;

            sprite.type = Sprite.Type.passive;
            sprite.initAssets();

            gameObjects.add(sprite);
        }
        else if (selected.startsWith("objects"))
        {
            TextureRegion tileRegion = atlas.findRegion("environment/tiles/1");

            float tileRegionWidth = tileRegion.getRegionWidth();

            TextureRegion region = atlas.findRegion(textureName);

            float width = 1 / (tileRegionWidth / region.getRegionWidth());

            Sprite sprite = new Sprite(3, 3, width, width / ((float) region.getRegionWidth() / (float) region.getRegionHeight()));
            sprite.textureAtlas = Assets.DEFAULT_ATLAS;
            sprite.textureName = textureName;

            sprite.type = Sprite.Type.passive;
            sprite.initAssets();

            gameObjects.add(sprite);
        }
        else if (selected.startsWith("coin"))
        {
            Coin coin = new Coin(3, 3, Coin.DEF_SIZE, Coin.DEF_SIZE, selected.contains("red"));
            coin.initAssets();

            gameObjects.add(coin);
        }
        else if (selected.startsWith("collider"))
        {
            Collider collider = new Collider(3, 3, 1, 1);

            gameObjects.insert(0, collider);
        }

    }

    private float invertY(float y)
    {
        return height - y;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button)
    {
        stage.touchUp(x, y, pointer, button);
        dragging = false;

        if (pressedKeys.contains(Input.Keys.CONTROL_LEFT))
        {
            snapToGrid();
        }

        return true;
    }

    private void snapToGrid()
    {
        if (draggingObject == null || dragging)
            return;
        //snap to grid
        int pX = Math.round(draggingObject.bounds.x);
        int pY = Math.round(draggingObject.bounds.y);
        draggingObject.position.set(pX, pY);
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer)
    {
        if(stage.touchDragged(x, y, pointer))
            return true;
        if (draggingObject != null)
        {

            if (horizontalResizeHover)
            {
                float myX = x / (width / cam.viewportWidth);
                boolean isLeft = Math.abs(myX - draggingObject.bounds.x) < Math.abs(myX - (draggingObject.bounds.x + draggingObject.bounds.width));
                if (isLeft)
                {
                    //must change both position and width
                    if(myX < draggingObject.bounds.x + draggingObject.bounds.width)
                    {
                        float diff = draggingObject.bounds.x - myX;
                        draggingObject.bounds.width += diff;
                        draggingObject.position.x = myX;
                    }
                }
                else
                {
                    //set width
                    if (myX > draggingObject.bounds.x)
                        draggingObject.bounds.width = myX - draggingObject.bounds.x;
                }
            }
            else if(verticalResizeHover)
            {
                float myY = invertY(y) / (height / cam.viewportHeight);

                boolean isBottom = Math.abs(myY - draggingObject.bounds.y) < Math.abs(myY - (draggingObject.bounds.y + draggingObject.bounds.height));
                if (isBottom)
                {
                    //must change both position and width
                    if(myY < draggingObject.bounds.y + draggingObject.bounds.height)
                    {
                        float diff = draggingObject.bounds.y - myY;
                        draggingObject.bounds.height += diff;
                        draggingObject.position.y = myY;
                    }
                }
                else
                {
                    //set width
                    if (myY > draggingObject.bounds.y)
                        draggingObject.bounds.height = myY - draggingObject.bounds.y;
                }
            }
            else
            {
                float myY = invertY(y) / (height / cam.viewportHeight) - downDistanceY;
                float myX = x / (width / cam.viewportWidth) - downDistanceX;
                dragging = true;
                draggingObject.position.set(myX, myY);
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        stage.scrolled(amount);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        if (dragging)
            return false;
        if (scrollPane.isVisible())
            stage.mouseMoved(screenX, screenY);

        if (!scrollPane.isVisible() && screenX <= 20)
        {
            scrollPane.setVisible(true);
        }
        if (scrollPane.isVisible() && screenX > 200)
        {
            scrollPane.setVisible(false);
        }

        float y = invertY(screenY) / (height / cam.viewportHeight);
        float x = screenX / (width / cam.viewportWidth);

        boolean changedV = false;
        boolean changedH = false;

        //tmp1 is outer rect
        //tmp2 is inner rect

        for (GameObject gameObject : gameObjects)
        {
            tmp1.set(gameObject.bounds.x - RESIZE_OFFSET, gameObject.bounds.y - RESIZE_OFFSET, gameObject.bounds.width + RESIZE_OFFSET * 2, gameObject.bounds.height + RESIZE_OFFSET * 2);

            if (!tmp1.contains(x, y))
                continue;

            tmp2.set(gameObject.bounds.x + RESIZE_OFFSET, gameObject.bounds.y + RESIZE_OFFSET, gameObject.bounds.width - RESIZE_OFFSET * 2, gameObject.bounds.height - RESIZE_OFFSET * 2);

            if ((x > tmp1.x && x < tmp2.x) || (x > tmp2.x + tmp2.width && x < tmp1.x + tmp1.width))
            {
                changedH = true;
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                break;
            }

            if ((y > tmp1.y && y < tmp2.y) || (y > tmp2.y + tmp2.height && y < tmp1.y + tmp1.height))
            {
                changedV = true;
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                break;
            }
        }

        if (!changedV && !changedH)
        {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
        verticalResizeHover = changedV;
        horizontalResizeHover = changedH;

        return false;
    }
}
