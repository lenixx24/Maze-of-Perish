package ua.edu.ukma.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Objects;

public class InstWindow extends StackPane {

    private final ImageView imageView;
    private GameMapView mapView;
    private Runnable onOpenCallback;
    private Runnable onCloseCallback;

    private final VBox rightPageContent;
    private final Label pageTitleLabel;
    private final Label pageDescriptionLabel;
    private final ImageView pageImageView;
    private final ImageView pageSecondImageView;
    private final HBox navigationContainer;
    private final Button btnPrev;
    private final Button btnNext;
    private final Label pageNumberLabel;

    private static class PageData {
        String title;
        String description;
        String imagePath;
        String secondImagePath;

        PageData(String title, String description, String imagePath, String secondImagePath) {
            this.title = title;
            this.description = description;
            this.imagePath = imagePath;
            this.secondImagePath = secondImagePath;
        }
    }
    private PageData[] currentCategoryPages;
    private int currentPageIndex = 0;

    public InstWindow() {
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("inst-root");
        this.setOpacity(0.0);
        this.setVisible(false);

        String cssPath = Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm();
        this.getStylesheets().add(cssPath);
        Font.loadFont(getClass().getResourceAsStream("/font/jersey10.ttf"), 16);

        Image instructionImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/inst.png")));
        this.imageView = new ImageView(instructionImage);
        this.imageView.setFitWidth(1000);
        this.imageView.setPreserveRatio(true);
        this.imageView.setSmooth(true);
        this.getChildren().add(imageView);

        HBox pagesContainer = new HBox();
        pagesContainer.setAlignment(Pos.CENTER);
        pagesContainer.setMaxSize(900, 520);
        pagesContainer.setTranslateY(-10);
        VBox leftPage = new VBox(20);
        leftPage.setAlignment(Pos.TOP_LEFT);
        leftPage.setPrefWidth(450);
        leftPage.setPadding(new Insets(60, 40, 40, 95));

        Label menuTitle = new Label("INSTRUCTIONS");
        menuTitle.getStyleClass().add("menu-title");
        leftPage.getChildren().add(menuTitle);

        Button btnRules = createMenuButton("-> Rules");
        Button btnTowers = createMenuButton("-> Defense types");
        Button btnEnemies = createMenuButton("-> Enemy types");

        leftPage.getChildren().addAll(btnRules, btnTowers, btnEnemies);

        VBox rightPage = new VBox(10);
        rightPage.setAlignment(Pos.TOP_LEFT);
        rightPage.setPrefWidth(500);
        rightPage.setPadding(new Insets(70, 60, 40, 120));

        rightPageContent = new VBox(12);
        rightPageContent.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(rightPageContent, Priority.ALWAYS);

        pageTitleLabel = new Label("");
        pageTitleLabel.getStyleClass().add("inst-page-title");

        pageImageView = new ImageView();
        pageImageView.setFitHeight(50);
        pageImageView.setPreserveRatio(true);
        pageImageView.setSmooth(true);

        pageSecondImageView = new ImageView();
        pageSecondImageView.setFitHeight(50);
        pageSecondImageView.setPreserveRatio(true);
        pageSecondImageView.setSmooth(true);

        HBox imagesContainer = new HBox(15);
        imagesContainer.getChildren().addAll(pageImageView, pageSecondImageView);

        pageDescriptionLabel = new Label("");
        pageDescriptionLabel.getStyleClass().add("inst-page-description");
        pageDescriptionLabel.setWrapText(true);
        pageDescriptionLabel.setMaxWidth(320);

        rightPageContent.getChildren().addAll(pageTitleLabel, imagesContainer, pageDescriptionLabel);

        navigationContainer = new HBox(15);
        navigationContainer.setAlignment(Pos.CENTER);
        navigationContainer.setPadding(new Insets(10, 0, 0, 0));
        navigationContainer.setMaxWidth(320);
        navigationContainer.setVisible(false);

        btnPrev = new Button("◀");
        btnPrev.getStyleClass().add("inst-nav-btn");

        pageNumberLabel = new Label("1/1");
        pageNumberLabel.getStyleClass().add("inst-page-num");

        btnNext = new Button("▶");
        btnNext.getStyleClass().add("inst-nav-btn");

        navigationContainer.getChildren().addAll(btnPrev, pageNumberLabel, btnNext);
        rightPage.getChildren().addAll(rightPageContent, navigationContainer);

        pagesContainer.getChildren().addAll(leftPage, rightPage);
        this.getChildren().add(pagesContainer);

        PageData[] rulesPages = new PageData[] {
                new PageData("BASIC RULES",
                        "- Protect the tower.\n\n- Build some defenses during the Preparation phase.\n\n- Move to collect gold.\n\n- Defeat all waves to win.",
                        "/image/hero.png", "/image/tower.png")
        };

        PageData[] defensePages = new PageData[] {
                new PageData("TRAP", "- Triggers when an enemy steps on it.\n\n- Deals instant damage to a single enemy.\n\n- Disappears after triggering.", "/image/trap.png",""),
                new PageData("BOMB", "- Triggers after enemies appear nearby.\n\n- Explodes within a radius, dealing damage to all enemies in the area.\n\n- Disappears after exploding.", "/image/bomb.png",""),
                new PageData("TURRET", "- Targets the nearest enemy within its range.\n\n- Fires periodically at the target.\n\n- Has its own health and can be destroyed", "/image/turret.png",""),
                new PageData("FREEZE", "- Creates a localized zone of cold.\n\n- All enemies within the zone move slower.\n\n- Doesn't deal direct damage.", "/image/freeze.png",""),
                new PageData("POISON CLOUD", "- Creates a zone that deals damage over time.\n\n- Enemies inside gradually lose health.\n\n- Highly effective against large groups of enemies.", "/image/poison.png",""),
                new PageData("BARRIER", "- Does not block the path entirely, but delays enemies.\n\n- Has its own health and can be destroyed.", "/image/barrier.png",""),
                new PageData("SNIPER TOWER", "- Has long range, fires slowly, targets only one enemy at a time.\n\n- Best suited against tough enemies.\n\n- Has its own health, can be destroyed.", "/image/sniper.png",""),
                new PageData("LASER TOWER", "- Fires a continuous laser beam toward enemies.\n\n- Deals low but constant damage.\n\n- Has its own health and can be destroyed.", "/image/laser.png",""),
                new PageData("CANNON TOWER", "- Fires missile that deal area damage.\n\n- Works great against groups of enemies.\n\n- Has a slow reload speed.\n\n- Has its own health and can be destroyed.", "/image/cannon.png","")
        };

        PageData[] enemyPages = new PageData[] {
                new PageData("WANDERER", "- Average speed.\n\n- Average health.\n\n- Deployed in most waves.", "/image/wanderer.png",""),
                new PageData("RAM", "- Has high health range.\n\n- Moves slower.\n\n- Effective at breaking through defenses and destroying structures.", "/image/ram.png",""),
                new PageData("DESTROYER", "- Deals increased damage to defensive structures and Barriers.\n\n- Moves faster.\n\n- Mix and match different defense types to destroy him.", "/image/destroyer.png","")
        };

        btnRules.setOnAction(e -> loadCategory(rulesPages));
        btnTowers.setOnAction(e -> loadCategory(defensePages));
        btnEnemies.setOnAction(e -> loadCategory(enemyPages));

        loadCategory(rulesPages);

        btnPrev.setOnAction(e -> {
            if (currentPageIndex > 0) {
                currentPageIndex--;
                displayCurrentPage();
            }
        });
        btnNext.setOnAction(e -> {
            if (currentCategoryPages != null && currentPageIndex < currentCategoryPages.length - 1) {
                currentPageIndex++;
                displayCurrentPage();
            }
        });
        this.setOnMouseClicked(e -> {
            if (e.getPickResult().getIntersectedNode() == this || e.getPickResult().getIntersectedNode() == imageView) {
                hide();
            }
        });
    }

    private void loadCategory(PageData[] pages) {
        this.currentCategoryPages = pages;
        this.currentPageIndex = 0;
        navigationContainer.setVisible(pages.length > 1);
        displayCurrentPage();
    }

    private void displayCurrentPage() {
        if (currentCategoryPages == null || currentCategoryPages.length == 0) return;
        PageData page = currentCategoryPages[currentPageIndex];
        pageTitleLabel.setText(page.title);
        pageDescriptionLabel.setText(page.description);
        pageNumberLabel.setText((currentPageIndex + 1) + "/" + currentCategoryPages.length);

        if (page.imagePath != null && !page.imagePath.isEmpty()) {
            try {
                var stream = getClass().getResourceAsStream(page.imagePath);
                if (stream != null) {
                    pageImageView.setImage(new Image(stream));
                    pageImageView.setVisible(true);
                    pageImageView.setManaged(true);
                } else {
                    pageImageView.setVisible(false);
                    pageImageView.setManaged(false);
                }
            } catch (Exception e) {
                pageImageView.setVisible(false);
                pageImageView.setManaged(false);
            }
        } else {
            pageImageView.setVisible(false);
            pageImageView.setManaged(false);
        }
        if (page.secondImagePath != null && !page.secondImagePath.isEmpty()) {
            try {
                var stream = getClass().getResourceAsStream(page.secondImagePath);
                if (stream != null) {
                    pageSecondImageView.setImage(new Image(stream));
                    pageSecondImageView.setVisible(true);
                    pageSecondImageView.setManaged(true);
                } else {
                    pageSecondImageView.setVisible(false);
                    pageSecondImageView.setManaged(false);
                }
            } catch (Exception e) {
                pageSecondImageView.setVisible(false);
                pageSecondImageView.setManaged(false);
            }
        } else {
            pageSecondImageView.setVisible(false);
            pageSecondImageView.setManaged(false);
        }
        btnPrev.setDisable(currentPageIndex == 0);
        btnNext.setDisable(currentPageIndex == currentCategoryPages.length - 1);
        btnPrev.setOpacity(currentPageIndex == 0?0.3:1.0);
        btnNext.setOpacity(currentPageIndex == currentCategoryPages.length - 1?0.3:1.0);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("menu-button");
        button.setFocusTraversable(false);
        return button;
    }

    public void show(Runnable onOpen, Runnable onClose) {
        this.onOpenCallback = onOpen;
        this.onCloseCallback = onClose;
        if (onOpenCallback != null) onOpenCallback.run();

        this.setVisible(true);
        this.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void hide() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), this);
        fadeOut.setFromValue(this.getOpacity());
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            this.setVisible(false);
            if (onCloseCallback != null) onCloseCallback.run();
            if (mapView != null) mapView.requestFocus();
        });
        fadeOut.play();
    }

    public void setMapView(GameMapView mapView) {
        this.mapView = mapView;
    }
}