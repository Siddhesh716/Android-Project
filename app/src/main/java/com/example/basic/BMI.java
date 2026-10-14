package com.example.basic;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Locale;

public class BMI extends AppCompatActivity {

    // ============================================================
    // MEASUREMENT CONTROLS
    // ============================================================

    private EditText heightInput;
    private EditText heightFeetInput;
    private EditText heightInchesInput;

    private EditText weightInput;

    private ImageButton heightMinusButton;
    private ImageButton heightPlusButton;
    private ImageButton weightMinusButton;
    private ImageButton weightPlusButton;

    private SeekBar heightSeekBar;
    private SeekBar weightSeekBar;

    // ============================================================
    // UNIT SELECTORS
    // ============================================================

    private Spinner heightUnitSpinner;
    private Spinner weightUnitSpinner;

    private TextView heightUnitLabel;
    private TextView weightUnitLabel;

    private TextView heightMinLabel;
    private TextView heightMiddleLabel;
    private TextView heightMaxLabel;

    private TextView weightMinLabel;
    private TextView weightMiddleLabel;
    private TextView weightMaxLabel;

    // ============================================================
    // BUTTONS
    // ============================================================

    private Button calculateButton;
    private Button recalculateButton;

    // ============================================================
    // BMI RESULT VIEWS
    // ============================================================

    private TextView bmiValueText;
    private TextView bmiCategoryText;
    private TextView bmiStatusText;
    private TextView interpretationText;
    private TextView scaleIndicatorText;
    private TextView scaleValueText;

    // ============================================================
    // CARDS
    // ============================================================

    private CardView resultCard;
    private CardView interpretationCard;

    // ============================================================
    // BMI SCALE
    // ============================================================

    private FrameLayout bmiScaleTrack;

    // ============================================================
    // BMI RANGES
    // ============================================================

    private LinearLayout rangesHeader;
    private LinearLayout rangesContent;

    private TextView rangesArrow;

    private LinearLayout severeThinnessRow;
    private LinearLayout moderateThinnessRow;
    private LinearLayout mildThinnessRow;
    private LinearLayout normalWeightRow;
    private LinearLayout overweightRow;
    private LinearLayout obesityClass1Row;
    private LinearLayout obesityClass2Row;
    private LinearLayout obesityClass3Row;

    private boolean rangesExpanded = false;

    // ============================================================
    // UNIT CONSTANTS
    // ============================================================

    private static final int HEIGHT_CM = 0;
    private static final int HEIGHT_FEET_INCHES = 1;

    private static final int WEIGHT_KG = 0;
    private static final int WEIGHT_LB = 1;

    // ============================================================
    // LIMITS
    // ============================================================

    private static final float MIN_HEIGHT_CM = 50f;
    private static final float MAX_HEIGHT_CM = 250f;

    private static final float MIN_WEIGHT_KG = 10f;
    private static final float MAX_WEIGHT_KG = 300f;

    private static final float CM_PER_INCH = 2.54f;
    private static final float KG_PER_LB = 0.45359237f;
    private static final float LB_PER_KG = 2.20462262f;

    // ============================================================
    // STATE
    // ============================================================

    private boolean isUpdatingUnits = false;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bmi);

        initializeViews();

        setupUnitSpinners();

        setupInputControls();

        setupSeekBars();

        setupCalculateButton();

        setupReferenceSection();

        setupRecalculateButton();

        // --------------------------------------------------------
        // Default values
        // --------------------------------------------------------

        heightUnitSpinner.setSelection(HEIGHT_CM);
        weightUnitSpinner.setSelection(WEIGHT_KG);

        heightInput.setText("170");
        weightInput.setText("60");

        heightFeetInput.setText("5");
        heightInchesInput.setText("7");

        heightSeekBar.setMax(200);
        weightSeekBar.setMax(290);

        heightSeekBar.setProgress(120);
        weightSeekBar.setProgress(50);

        updateHeightUnitUI();
        updateWeightUnitUI();

        // Initially unlocked
        setMeasurementControlsEnabled(true);
    }

    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        // --------------------------------------------------------
        // Height
        // --------------------------------------------------------

        heightInput = findViewById(R.id.heightInput);

        heightFeetInput = findViewById(R.id.heightFeetInput);

        heightInchesInput = findViewById(R.id.heightInchesInput);

        heightMinusButton = findViewById(R.id.heightMinusButton);

        heightPlusButton = findViewById(R.id.heightPlusButton);

        heightSeekBar = findViewById(R.id.heightSeekBar);

        heightUnitSpinner = findViewById(R.id.heightUnitSpinner);

        heightUnitLabel = findViewById(R.id.heightUnitLabel);

        heightMinLabel = findViewById(R.id.heightMinLabel);

        heightMiddleLabel = findViewById(R.id.heightMiddleLabel);

        heightMaxLabel = findViewById(R.id.heightMaxLabel);

        // --------------------------------------------------------
        // Weight
        // --------------------------------------------------------

        weightInput = findViewById(R.id.weightInput);

        weightMinusButton = findViewById(R.id.weightMinusButton);

        weightPlusButton = findViewById(R.id.weightPlusButton);

        weightSeekBar = findViewById(R.id.weightSeekBar);

        weightUnitSpinner = findViewById(R.id.weightUnitSpinner);

        weightUnitLabel = findViewById(R.id.weightUnitLabel);

        weightMinLabel = findViewById(R.id.weightMinLabel);

        weightMiddleLabel = findViewById(R.id.weightMiddleLabel);

        weightMaxLabel = findViewById(R.id.weightMaxLabel);

        // --------------------------------------------------------
        // Buttons
        // --------------------------------------------------------

        calculateButton = findViewById(R.id.calculateButton);

        recalculateButton = findViewById(R.id.recalculateButton);

        // --------------------------------------------------------
        // Result
        // --------------------------------------------------------

        bmiValueText = findViewById(R.id.bmiValueText);

        bmiCategoryText = findViewById(R.id.bmiCategoryText);

        bmiStatusText = findViewById(R.id.bmiStatusText);

        interpretationText = findViewById(R.id.interpretationText);

        scaleIndicatorText = findViewById(R.id.scaleIndicatorText);

        scaleValueText = findViewById(R.id.scaleValueText);

        // --------------------------------------------------------
        // Cards
        // --------------------------------------------------------

        resultCard = findViewById(R.id.resultCard);

        interpretationCard = findViewById(R.id.interpretationCard);

        // --------------------------------------------------------
        // Scale
        // --------------------------------------------------------

        bmiScaleTrack = findViewById(R.id.bmiScaleTrack);

        // --------------------------------------------------------
        // BMI ranges
        // --------------------------------------------------------

        rangesHeader = findViewById(R.id.rangesHeader);

        rangesContent = findViewById(R.id.rangesContent);

        rangesArrow = findViewById(R.id.rangesArrow);

        severeThinnessRow = findViewById(R.id.severeThinnessRow);

        moderateThinnessRow = findViewById(R.id.moderateThinnessRow);

        mildThinnessRow = findViewById(R.id.mildThinnessRow);

        normalWeightRow = findViewById(R.id.normalWeightRow);

        overweightRow = findViewById(R.id.overweightRow);

        obesityClass1Row = findViewById(R.id.obesityClass1Row);

        obesityClass2Row = findViewById(R.id.obesityClass2Row);

        obesityClass3Row = findViewById(R.id.obesityClass3Row);
    }

    // ============================================================
    // UNIT SPINNERS
    // ============================================================

    private void setupUnitSpinners() {

        String[] heightUnits = {
                "cm",
                "ft + in"
        };

        String[] weightUnits = {
                "kg",
                "lb"
        };

        ArrayAdapter<String> heightAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        heightUnits
                );

        heightAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        ArrayAdapter<String> weightAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        weightUnits
                );

        weightAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        heightUnitSpinner.setAdapter(heightAdapter);

        weightUnitSpinner.setAdapter(weightAdapter);

        // ========================================================
        // HEIGHT UNIT CHANGE
        // ========================================================

        heightUnitSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (isUpdatingUnits) {
                            return;
                        }

                        if (!areMeasurementsEnabled()) {
                            return;
                        }

                        updateHeightUnitUI();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        // ========================================================
        // WEIGHT UNIT CHANGE
        // ========================================================

        weightUnitSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (isUpdatingUnits) {
                            return;
                        }

                        if (!areMeasurementsEnabled()) {
                            return;
                        }

                        updateWeightUnitUI();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
    }

    // ============================================================
    // UPDATE HEIGHT UNIT UI
    // ============================================================

    private void updateHeightUnitUI() {

        if (!areMeasurementsEnabled()) {
            return;
        }

        float heightCm = getCurrentHeightCm();

        if (heightCm < MIN_HEIGHT_CM ||
                heightCm > MAX_HEIGHT_CM) {

            heightCm = 170f;
        }

        int selectedUnit =
                heightUnitSpinner.getSelectedItemPosition();

        if (selectedUnit == HEIGHT_CM) {

            // ----------------------------------------------------
            // CM MODE
            // ----------------------------------------------------

            heightInput.setVisibility(View.VISIBLE);

            heightFeetInput.setVisibility(View.GONE);

            heightInchesInput.setVisibility(View.GONE);

            heightUnitLabel.setVisibility(View.VISIBLE);

            heightUnitLabel.setText("cm");

            heightInput.setText(
                    String.format(
                            Locale.US,
                            "%.0f",
                            heightCm
                    )
            );

            heightMinLabel.setText("50");

            heightMiddleLabel.setText("150");

            heightMaxLabel.setText("250");

        } else {

            // ----------------------------------------------------
            // FEET + INCHES MODE
            // ----------------------------------------------------

            heightInput.setVisibility(View.GONE);

            heightFeetInput.setVisibility(View.VISIBLE);

            heightInchesInput.setVisibility(View.VISIBLE);

            heightUnitLabel.setVisibility(View.GONE);

            setFeetAndInchesFromCm(heightCm);

            heightMinLabel.setText("1' 8\"");

            heightMiddleLabel.setText("4' 11\"");

            heightMaxLabel.setText("8' 2\"");
        }

        updateHeightSeekBar(heightCm);
    }

    // ============================================================
    // UPDATE WEIGHT UNIT UI
    // ============================================================

    private void updateWeightUnitUI() {

        if (!areMeasurementsEnabled()) {
            return;
        }

        float weightKg = getCurrentWeightKg();

        if (weightKg < MIN_WEIGHT_KG ||
                weightKg > MAX_WEIGHT_KG) {

            weightKg = 60f;
        }

        int selectedUnit =
                weightUnitSpinner.getSelectedItemPosition();

        if (selectedUnit == WEIGHT_KG) {

            // ----------------------------------------------------
            // KG MODE
            // ----------------------------------------------------

            weightUnitLabel.setText("kg");

            weightInput.setText(
                    String.format(
                            Locale.US,
                            "%.0f",
                            weightKg
                    )
            );

            weightMinLabel.setText("10");

            weightMiddleLabel.setText("150");

            weightMaxLabel.setText("300");

        } else {

            // ----------------------------------------------------
            // LB MODE
            // ----------------------------------------------------

            float pounds =
                    weightKg * LB_PER_KG;

            weightUnitLabel.setText("lb");

            weightInput.setText(
                    String.format(
                            Locale.US,
                            "%.1f",
                            pounds
                    )
            );

            weightMinLabel.setText("22");

            weightMiddleLabel.setText("331");

            weightMaxLabel.setText("661");
        }

        updateWeightSeekBar(weightKg);
    }

    // ============================================================
    // INPUT CONTROLS
    // ============================================================

    private void setupInputControls() {

        // --------------------------------------------------------
        // Input types
        // --------------------------------------------------------

        heightInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        heightFeetInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        heightInchesInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        weightInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        // ========================================================
        // HEIGHT MINUS
        // ========================================================

        heightMinusButton.setOnClickListener(v -> {

            if (!areMeasurementsEnabled()) {
                return;
            }

            if (heightUnitSpinner.getSelectedItemPosition()
                    == HEIGHT_CM) {

                float heightCm = getCurrentHeightCm();

                heightCm -= 1f;

                heightCm = clamp(
                        heightCm,
                        MIN_HEIGHT_CM,
                        MAX_HEIGHT_CM
                );

                setHeightFromCm(heightCm);

            } else {

                int totalInches =
                        getCurrentTotalHeightInches();

                totalInches--;

                totalInches =
                        clampInt(
                                totalInches,
                                20,
                                98
                        );

                setHeightFromTotalInches(totalInches);
            }
        });

        // ========================================================
        // HEIGHT PLUS
        // ========================================================

        heightPlusButton.setOnClickListener(v -> {

            if (!areMeasurementsEnabled()) {
                return;
            }

            if (heightUnitSpinner.getSelectedItemPosition()
                    == HEIGHT_CM) {

                float heightCm = getCurrentHeightCm();

                heightCm += 1f;

                heightCm = clamp(
                        heightCm,
                        MIN_HEIGHT_CM,
                        MAX_HEIGHT_CM
                );

                setHeightFromCm(heightCm);

            } else {

                int totalInches =
                        getCurrentTotalHeightInches();

                totalInches++;

                totalInches =
                        clampInt(
                                totalInches,
                                20,
                                98
                        );

                setHeightFromTotalInches(totalInches);
            }
        });

        // ========================================================
        // WEIGHT MINUS
        // ========================================================

        weightMinusButton.setOnClickListener(v -> {

            if (!areMeasurementsEnabled()) {
                return;
            }

            if (weightUnitSpinner.getSelectedItemPosition()
                    == WEIGHT_KG) {

                float weightKg =
                        getCurrentWeightKg();

                weightKg -= 1f;

                weightKg = clamp(
                        weightKg,
                        MIN_WEIGHT_KG,
                        MAX_WEIGHT_KG
                );

                setWeightFromKg(weightKg);

            } else {

                float pounds =
                        getCurrentWeightPounds();

                pounds -= 1f;

                pounds = clamp(
                        pounds,
                        MIN_WEIGHT_KG * LB_PER_KG,
                        MAX_WEIGHT_KG * LB_PER_KG
                );

                setWeightFromPounds(pounds);
            }
        });

        // ========================================================
        // WEIGHT PLUS
        // ========================================================

        weightPlusButton.setOnClickListener(v -> {

            if (!areMeasurementsEnabled()) {
                return;
            }

            if (weightUnitSpinner.getSelectedItemPosition()
                    == WEIGHT_KG) {

                float weightKg =
                        getCurrentWeightKg();

                weightKg += 1f;

                weightKg = clamp(
                        weightKg,
                        MIN_WEIGHT_KG,
                        MAX_WEIGHT_KG
                );

                setWeightFromKg(weightKg);

            } else {

                float pounds =
                        getCurrentWeightPounds();

                pounds += 1f;

                pounds = clamp(
                        pounds,
                        MIN_WEIGHT_KG * LB_PER_KG,
                        MAX_WEIGHT_KG * LB_PER_KG
                );

                setWeightFromPounds(pounds);
            }
        });

        // ========================================================
        // HEIGHT CM MANUAL INPUT
        // ========================================================

        heightInput.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (!hasFocus &&
                            areMeasurementsEnabled() &&
                            heightInput.getVisibility()
                                    == View.VISIBLE) {

                        try {

                            String value =
                                    heightInput
                                            .getText()
                                            .toString()
                                            .trim();

                            if (value.isEmpty()) {
                                setHeightFromCm(170f);
                                return;
                            }

                            float height =
                                    Float.parseFloat(value);

                            if (height < MIN_HEIGHT_CM ||
                                    height > MAX_HEIGHT_CM) {

                                heightInput.setError(
                                        "Height must be between 50 and 250 cm"
                                );

                                height =
                                        clamp(
                                                height,
                                                MIN_HEIGHT_CM,
                                                MAX_HEIGHT_CM
                                        );
                            }

                            setHeightFromCm(height);

                        } catch (Exception e) {

                            setHeightFromCm(170f);
                        }
                    }
                }
        );

        // ========================================================
        // HEIGHT FEET INPUT
        // ========================================================

        heightFeetInput.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (!hasFocus &&
                            areMeasurementsEnabled() &&
                            heightFeetInput.getVisibility()
                                    == View.VISIBLE) {

                        normalizeFeetInches();
                    }
                }
        );

        // ========================================================
        // HEIGHT INCH INPUT
        // ========================================================

        heightInchesInput.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (!hasFocus &&
                            areMeasurementsEnabled() &&
                            heightInchesInput.getVisibility()
                                    == View.VISIBLE) {

                        normalizeFeetInches();
                    }
                }
        );

        // ========================================================
        // WEIGHT MANUAL INPUT
        // ========================================================

        weightInput.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (!hasFocus &&
                            areMeasurementsEnabled()) {

                        try {

                            String value =
                                    weightInput
                                            .getText()
                                            .toString()
                                            .trim();

                            if (value.isEmpty()) {
                                setWeightFromKg(60f);
                                return;
                            }

                            float entered =
                                    Float.parseFloat(value);

                            if (weightUnitSpinner
                                    .getSelectedItemPosition()
                                    == WEIGHT_KG) {

                                if (entered < MIN_WEIGHT_KG ||
                                        entered > MAX_WEIGHT_KG) {

                                    weightInput.setError(
                                            "Weight must be between 10 and 300 kg"
                                    );
                                }

                                entered =
                                        clamp(
                                                entered,
                                                MIN_WEIGHT_KG,
                                                MAX_WEIGHT_KG
                                        );

                                setWeightFromKg(entered);

                            } else {

                                float minLb =
                                        MIN_WEIGHT_KG * LB_PER_KG;

                                float maxLb =
                                        MAX_WEIGHT_KG * LB_PER_KG;

                                if (entered < minLb ||
                                        entered > maxLb) {

                                    weightInput.setError(
                                            "Weight must be between 22 and 661 lb"
                                    );
                                }

                                entered =
                                        clamp(
                                                entered,
                                                minLb,
                                                maxLb
                                        );

                                setWeightFromPounds(entered);
                            }

                        } catch (Exception e) {

                            setWeightFromKg(60f);
                        }
                    }
                }
        );
    }

    // ============================================================
    // SEEK BARS
    // ============================================================

    private void setupSeekBars() {

        // ========================================================
        // HEIGHT SEEKBAR
        // ========================================================

        heightSeekBar.setMax(200);

        heightSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (!areMeasurementsEnabled()) {
                            return;
                        }

                        if (!fromUser) {
                            return;
                        }

                        if (heightUnitSpinner
                                .getSelectedItemPosition()
                                == HEIGHT_CM) {

                            float heightCm =
                                    MIN_HEIGHT_CM + progress;

                            setHeightFromCm(heightCm);

                        } else {

                            /*
                             * Height SeekBar represents
                             * 20–98 total inches.
                             */
                            int totalInches =
                                    20 + progress;

                            setHeightFromTotalInches(
                                    totalInches
                            );
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {
                    }
                }
        );

        // ========================================================
        // WEIGHT SEEKBAR
        // ========================================================

        weightSeekBar.setMax(290);

        weightSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (!areMeasurementsEnabled()) {
                            return;
                        }

                        if (!fromUser) {
                            return;
                        }

                        if (weightUnitSpinner
                                .getSelectedItemPosition()
                                == WEIGHT_KG) {

                            float weightKg =
                                    MIN_WEIGHT_KG + progress;

                            setWeightFromKg(weightKg);

                        } else {

                            /*
                             * 22–661 lb.
                             */
                            float pounds =
                                    22f + progress;

                            setWeightFromPounds(pounds);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {
                    }
                }
        );
    }

    // ============================================================
    // GET CURRENT HEIGHT IN CM
    // ============================================================

    private float getCurrentHeightCm() {

        try {

            int selectedUnit =
                    heightUnitSpinner.getSelectedItemPosition();

            // ----------------------------------------------------
            // CM
            // ----------------------------------------------------

            if (selectedUnit == HEIGHT_CM) {

                String value =
                        heightInput.getText()
                                .toString()
                                .trim();

                if (value.isEmpty()) {
                    return 170f;
                }

                return Float.parseFloat(value);
            }

            // ----------------------------------------------------
            // FEET + INCHES
            // ----------------------------------------------------

            String feetString =
                    heightFeetInput.getText()
                            .toString()
                            .trim();

            String inchesString =
                    heightInchesInput.getText()
                            .toString()
                            .trim();

            float feet =
                    feetString.isEmpty()
                            ? 0f
                            : Float.parseFloat(feetString);

            float inches =
                    inchesString.isEmpty()
                            ? 0f
                            : Float.parseFloat(inchesString);

            return (
                    (feet * 12f) + inches
            ) * CM_PER_INCH;

        } catch (Exception e) {

            return 170f;
        }
    }

    // ============================================================
    // GET CURRENT WEIGHT IN KG
    // ============================================================

    private float getCurrentWeightKg() {

        try {

            String value =
                    weightInput.getText()
                            .toString()
                            .trim();

            if (value.isEmpty()) {
                return 60f;
            }

            float entered =
                    Float.parseFloat(value);

            if (weightUnitSpinner
                    .getSelectedItemPosition()
                    == WEIGHT_KG) {

                return entered;

            } else {

                return entered * KG_PER_LB;
            }

        } catch (Exception e) {

            return 60f;
        }
    }

    // ============================================================
    // GET CURRENT WEIGHT IN POUNDS
    // ============================================================

    private float getCurrentWeightPounds() {

        try {

            String value =
                    weightInput.getText()
                            .toString()
                            .trim();

            if (value.isEmpty()) {
                return 60f * LB_PER_KG;
            }

            return Float.parseFloat(value);

        } catch (Exception e) {

            return 60f * LB_PER_KG;
        }
    }

    // ============================================================
    // GET CURRENT TOTAL HEIGHT INCHES
    // ============================================================

    private int getCurrentTotalHeightInches() {

        try {

            float feet =
                    Float.parseFloat(
                            heightFeetInput
                                    .getText()
                                    .toString()
                                    .trim()
                    );

            float inches = 0f;

            String inchesText =
                    heightInchesInput
                            .getText()
                            .toString()
                            .trim();

            if (!inchesText.isEmpty()) {

                inches =
                        Float.parseFloat(inchesText);
            }

            return Math.round(
                    (feet * 12f) + inches
            );

        } catch (Exception e) {

            return 67;
        }
    }

    // ============================================================
    // SET HEIGHT FROM CM
    // ============================================================

    private void setHeightFromCm(float heightCm) {

        heightCm =
                clamp(
                        heightCm,
                        MIN_HEIGHT_CM,
                        MAX_HEIGHT_CM
                );

        if (heightUnitSpinner
                .getSelectedItemPosition()
                == HEIGHT_CM) {

            // ----------------------------------------------------
            // CM DISPLAY
            // ----------------------------------------------------

            heightInput.setText(
                    String.format(
                            Locale.US,
                            "%.0f",
                            heightCm
                    )
            );

            int progress =
                    Math.round(
                            heightCm -
                                    MIN_HEIGHT_CM
                    );

            heightSeekBar.setMax(200);

            heightSeekBar.setProgress(
                    clampInt(
                            progress,
                            0,
                            200
                    )
            );

        } else {

            // ----------------------------------------------------
            // FT + IN DISPLAY
            // ----------------------------------------------------

            int totalInches =
                    Math.round(
                            heightCm /
                                    CM_PER_INCH
                    );

            totalInches =
                    clampInt(
                            totalInches,
                            20,
                            98
                    );

            setHeightFromTotalInches(
                    totalInches
            );
        }
    }

    // ============================================================
    // SET HEIGHT FROM TOTAL INCHES
    // ============================================================

    private void setHeightFromTotalInches(
            int totalInches) {

        totalInches =
                clampInt(
                        totalInches,
                        20,
                        98
                );

        int feet =
                totalInches / 12;

        int inches =
                totalInches % 12;

        heightFeetInput.setText(
                String.valueOf(feet)
        );

        heightInchesInput.setText(
                String.valueOf(inches)
        );

        // --------------------------------------------------------
        // SeekBar
        // --------------------------------------------------------

        heightSeekBar.setMax(78);

        heightSeekBar.setProgress(
                totalInches - 20
        );
    }

    // ============================================================
    // SET FEET + INCHES FROM CM
    // ============================================================

    private void setFeetAndInchesFromCm(
            float heightCm) {

        int totalInches =
                Math.round(
                        heightCm /
                                CM_PER_INCH
                );

        totalInches =
                clampInt(
                        totalInches,
                        20,
                        98
                );

        setHeightFromTotalInches(
                totalInches
        );
    }

    // ============================================================
    // SET WEIGHT FROM KG
    // ============================================================

    private void setWeightFromKg(
            float weightKg) {

        weightKg =
                clamp(
                        weightKg,
                        MIN_WEIGHT_KG,
                        MAX_WEIGHT_KG
                );

        if (weightUnitSpinner
                .getSelectedItemPosition()
                == WEIGHT_KG) {

            // ----------------------------------------------------
            // KG DISPLAY
            // ----------------------------------------------------

            weightInput.setText(
                    String.format(
                            Locale.US,
                            "%.0f",
                            weightKg
                    )
            );

            weightSeekBar.setMax(290);

            weightSeekBar.setProgress(
                    clampInt(
                            Math.round(
                                    weightKg -
                                            MIN_WEIGHT_KG
                            ),
                            0,
                            290
                    )
            );

        } else {

            // ----------------------------------------------------
            // LB DISPLAY
            // ----------------------------------------------------

            float pounds =
                    weightKg * LB_PER_KG;

            setWeightFromPounds(
                    pounds
            );
        }
    }

    // ============================================================
    // SET WEIGHT FROM POUNDS
    // ============================================================

    private void setWeightFromPounds(
            float pounds) {

        float minLb =
                MIN_WEIGHT_KG * LB_PER_KG;

        float maxLb =
                MAX_WEIGHT_KG * LB_PER_KG;

        pounds =
                clamp(
                        pounds,
                        minLb,
                        maxLb
                );

        weightInput.setText(
                String.format(
                        Locale.US,
                        "%.1f",
                        pounds
                )
        );

        weightSeekBar.setMax(
                Math.round(maxLb - 22f)
        );

        int progress =
                Math.round(
                        pounds - 22f
                );

        weightSeekBar.setProgress(
                clampInt(
                        progress,
                        0,
                        weightSeekBar.getMax()
                )
        );
    }

    // ============================================================
    // UPDATE HEIGHT SEEKBAR
    // ============================================================

    private void updateHeightSeekBar(
            float heightCm) {

        heightCm =
                clamp(
                        heightCm,
                        MIN_HEIGHT_CM,
                        MAX_HEIGHT_CM
                );

        if (heightUnitSpinner
                .getSelectedItemPosition()
                == HEIGHT_CM) {

            heightSeekBar.setMax(200);

            int progress =
                    Math.round(
                            heightCm -
                                    MIN_HEIGHT_CM
                    );

            heightSeekBar.setProgress(
                    clampInt(
                            progress,
                            0,
                            200
                    )
            );

        } else {

            heightSeekBar.setMax(78);

            int totalInches =
                    Math.round(
                            heightCm /
                                    CM_PER_INCH
                    );

            totalInches =
                    clampInt(
                            totalInches,
                            20,
                            98
                    );

            heightSeekBar.setProgress(
                    totalInches - 20
            );
        }
    }

    // ============================================================
    // UPDATE WEIGHT SEEKBAR
    // ============================================================

    private void updateWeightSeekBar(
            float weightKg) {

        weightKg =
                clamp(
                        weightKg,
                        MIN_WEIGHT_KG,
                        MAX_WEIGHT_KG
                );

        if (weightUnitSpinner
                .getSelectedItemPosition()
                == WEIGHT_KG) {

            weightSeekBar.setMax(290);

            int progress =
                    Math.round(
                            weightKg -
                                    MIN_WEIGHT_KG
                    );

            weightSeekBar.setProgress(
                    clampInt(
                            progress,
                            0,
                            290
                    )
            );

        } else {

            float pounds =
                    weightKg * LB_PER_KG;

            int progress =
                    Math.round(
                            pounds - 22f
                    );

            weightSeekBar.setMax(
                    Math.round(
                            MAX_WEIGHT_KG *
                                    LB_PER_KG -
                                    22f
                    )
            );

            weightSeekBar.setProgress(
                    clampInt(
                            progress,
                            0,
                            weightSeekBar.getMax()
                    )
            );
        }
    }

    // ============================================================
    // NORMALIZE FEET + INCHES
    // ============================================================

    private void normalizeFeetInches() {

        if (!areMeasurementsEnabled()) {
            return;
        }

        try {

            String feetText =
                    heightFeetInput
                            .getText()
                            .toString()
                            .trim();

            String inchesText =
                    heightInchesInput
                            .getText()
                            .toString()
                            .trim();

            if (feetText.isEmpty()) {
                feetText = "0";
            }

            if (inchesText.isEmpty()) {
                inchesText = "0";
            }

            float feet =
                    Float.parseFloat(feetText);

            float inches =
                    Float.parseFloat(inchesText);

            // ----------------------------------------------------
            // Negative values
            // ----------------------------------------------------

            if (feet < 0f) {
                feet = 0f;
            }

            if (inches < 0f) {
                inches = 0f;
            }

            // ----------------------------------------------------
            // Convert extra inches into feet
            // ----------------------------------------------------

            if (inches >= 12f) {

                feet +=
                        Math.floor(
                                inches / 12f
                        );

                inches =
                        inches % 12f;
            }

            // ----------------------------------------------------
            // Convert to centimeters
            // ----------------------------------------------------

            float heightCm =
                    (
                            feet * 12f +
                                    inches
                    ) * CM_PER_INCH;

            // ----------------------------------------------------
            // Validate
            // ----------------------------------------------------

            if (heightCm < MIN_HEIGHT_CM ||
                    heightCm > MAX_HEIGHT_CM) {

                heightFeetInput.setError(
                        "Height must be between 1'8\" and 8'2\""
                );
            }

            heightCm =
                    clamp(
                            heightCm,
                            MIN_HEIGHT_CM,
                            MAX_HEIGHT_CM
                    );

            // ----------------------------------------------------
            // Normalize display
            // ----------------------------------------------------

            setFeetAndInchesFromCm(
                    heightCm
            );

        } catch (Exception e) {

            setFeetAndInchesFromCm(
                    170f
            );
        }
    }

    // ============================================================
    // CALCULATE BUTTON
    // ============================================================

    private void setupCalculateButton() {

        calculateButton.setOnClickListener(
                v -> calculateBMI()
        );
    }

    // ============================================================
    // CALCULATE BMI
    // ============================================================

    private void calculateBMI() {

        hideKeyboard();

        // --------------------------------------------------------
        // Normalize ft/in before calculation
        // --------------------------------------------------------

        if (heightUnitSpinner
                .getSelectedItemPosition()
                == HEIGHT_FEET_INCHES) {

            normalizeFeetInches();
        }

        // --------------------------------------------------------
        // Get normalized values
        // --------------------------------------------------------

        float heightCm =
                getCurrentHeightCm();

        float weightKg =
                getCurrentWeightKg();

        // --------------------------------------------------------
        // Height validation
        // --------------------------------------------------------

        if (heightCm < MIN_HEIGHT_CM ||
                heightCm > MAX_HEIGHT_CM) {

            if (heightUnitSpinner
                    .getSelectedItemPosition()
                    == HEIGHT_CM) {

                heightInput.setError(
                        "Height must be between 50 and 250 cm"
                );

                heightInput.requestFocus();

            } else {

                heightFeetInput.setError(
                        "Height must be between 1'8\" and 8'2\""
                );

                heightFeetInput.requestFocus();
            }

            return;
        }

        // --------------------------------------------------------
        // Weight validation
        // --------------------------------------------------------

        if (weightKg < MIN_WEIGHT_KG ||
                weightKg > MAX_WEIGHT_KG) {

            if (weightUnitSpinner
                    .getSelectedItemPosition()
                    == WEIGHT_KG) {

                weightInput.setError(
                        "Weight must be between 10 and 300 kg"
                );

            } else {

                weightInput.setError(
                        "Weight must be between 22 and 661 lb"
                );
            }

            weightInput.requestFocus();

            return;
        }

        // --------------------------------------------------------
        // Convert height to meters
        // --------------------------------------------------------

        float heightMeters =
                heightCm / 100f;

        // --------------------------------------------------------
        // BMI calculation
        // --------------------------------------------------------

        float bmi =
                weightKg /
                        (
                                heightMeters *
                                        heightMeters
                        );

        String bmiCategory =
                getBMICategory(bmi);

        String bmiStatus =
                getBMIStatus(bmi);

        String interpretation =
                getInterpretation(bmi);

        // --------------------------------------------------------
        // Display result
        // --------------------------------------------------------

        displayBMIResult(
                bmi,
                bmiCategory,
                bmiStatus,
                interpretation
        );

        // --------------------------------------------------------
        // Lock measurement section
        // --------------------------------------------------------

        setMeasurementControlsEnabled(
                false
        );

        // --------------------------------------------------------
        // Button state
        // --------------------------------------------------------

        recalculateButton.setVisibility(
                View.VISIBLE
        );

        calculateButton.setVisibility(
                View.GONE
        );
    }

    // ============================================================
    // BMI CATEGORY
    // ============================================================

    private String getBMICategory(
            float bmi) {

        if (bmi < 16.0f) {

            return "Severe Thinness";

        } else if (bmi < 17.0f) {

            return "Moderate Thinness";

        } else if (bmi < 18.5f) {

            return "Mild Thinness";

        } else if (bmi < 25.0f) {

            return "Normal Weight";

        } else if (bmi < 30.0f) {

            return "Overweight";

        } else if (bmi < 35.0f) {

            return "Obesity Class 1";

        } else if (bmi < 40.0f) {

            return "Obesity Class 2";

        } else {

            return "Obesity Class 3";
        }
    }

    // ============================================================
    // BMI STATUS
    // ============================================================

    private String getBMIStatus(
            float bmi) {

        if (bmi < 18.5f) {

            return "Your BMI is below the healthy range.";

        } else if (bmi < 25.0f) {

            return "Your BMI is within the healthy range.";

        } else if (bmi < 30.0f) {

            return "Your BMI is above the healthy range.";

        } else {

            return "Your BMI is in the obesity range.";
        }
    }

    // ============================================================
    // INTERPRETATION
    // ============================================================

    private String getInterpretation(
            float bmi) {

        if (bmi < 16.0f) {

            return "Your BMI indicates severe thinness. "
                    + "Consider discussing your nutrition and "
                    + "health goals with a qualified professional.";

        } else if (bmi < 17.0f) {

            return "Your BMI indicates moderate thinness. "
                    + "A balanced nutrition and fitness plan may "
                    + "help support healthy weight management.";

        } else if (bmi < 18.5f) {

            return "Your BMI indicates mild thinness. "
                    + "Focus on balanced nutrition and "
                    + "consistent strength and fitness habits.";

        } else if (bmi < 25.0f) {

            return "Your BMI falls within the commonly defined "
                    + "healthy range. Continue maintaining balanced "
                    + "nutrition and regular physical activity.";

        } else if (bmi < 30.0f) {

            return "Your BMI is in the overweight range. "
                    + "Regular exercise and balanced nutrition "
                    + "can support healthy weight management.";

        } else if (bmi < 35.0f) {

            return "Your BMI falls within Obesity Class 1. "
                    + "Consider a structured fitness and nutrition "
                    + "plan and professional guidance when appropriate.";

        } else if (bmi < 40.0f) {

            return "Your BMI falls within Obesity Class 2. "
                    + "A structured approach to nutrition, activity, "
                    + "and professional guidance may be beneficial.";

        } else {

            return "Your BMI falls within Obesity Class 3. "
                    + "Consider speaking with a qualified healthcare "
                    + "professional about your health goals.";
        }
    }

    // ============================================================
    // DISPLAY BMI RESULT
    // ============================================================

    private void displayBMIResult(
            float bmi,
            String category,
            String status,
            String interpretation) {

        resultCard.setVisibility(
                View.VISIBLE
        );

        interpretationCard.setVisibility(
                View.VISIBLE
        );

        bmiValueText.setText(
                String.format(
                        Locale.US,
                        "%.1f",
                        bmi
                )
        );

        bmiCategoryText.setText(
                category
        );

        bmiStatusText.setText(
                status
        );

        interpretationText.setText(
                interpretation
        );

        scaleValueText.setText(
                String.format(
                        Locale.US,
                        "BMI %.1f",
                        bmi
                )
        );

        updateBMIScale(bmi);

        highlightRange(category);
    }

    // ============================================================
    // BMI SCALE
    // ============================================================

    private void updateBMIScale(
            float bmi) {

        if (bmiScaleTrack == null ||
                scaleIndicatorText == null) {

            return;
        }

        bmiScaleTrack.post(() -> {

            int trackWidth =
                    bmiScaleTrack.getWidth();

            if (trackWidth <= 0) {
                return;
            }

            float minBMI = 10f;

            float maxBMI = 40f;

            float clampedBMI =
                    Math.max(
                            minBMI,
                            Math.min(
                                    maxBMI,
                                    bmi
                            )
                    );

            float percentage =
                    (
                            clampedBMI -
                                    minBMI
                    ) /
                            (
                                    maxBMI -
                                            minBMI
                            );

            int indicatorWidth =
                    scaleIndicatorText.getWidth();

            float position =
                    percentage *
                            (
                                    trackWidth -
                                            indicatorWidth
                            );

            scaleIndicatorText.setTranslationX(
                    position
            );
        });
    }

    // ============================================================
    // BMI RANGE REFERENCE
    // ============================================================

    private void setupReferenceSection() {

        rangesContent.setVisibility(
                View.GONE
        );

        rangesArrow.setText("⌄");

        rangesHeader.setOnClickListener(
                v -> {

                    if (rangesExpanded) {

                        rangesContent.setVisibility(
                                View.GONE
                        );

                        rangesArrow.setText("⌄");

                        rangesExpanded = false;

                    } else {

                        rangesContent.setVisibility(
                                View.VISIBLE
                        );

                        rangesArrow.setText("⌃");

                        rangesExpanded = true;
                    }
                }
        );
    }

    // ============================================================
    // HIGHLIGHT CURRENT BMI RANGE
    // ============================================================

    private void highlightRange(
            String category) {

        resetRangeRows();

        LinearLayout selectedRow = null;

        switch (category) {

            case "Severe Thinness":
                selectedRow =
                        severeThinnessRow;
                break;

            case "Moderate Thinness":
                selectedRow =
                        moderateThinnessRow;
                break;

            case "Mild Thinness":
                selectedRow =
                        mildThinnessRow;
                break;

            case "Normal Weight":
                selectedRow =
                        normalWeightRow;
                break;

            case "Overweight":
                selectedRow =
                        overweightRow;
                break;

            case "Obesity Class 1":
                selectedRow =
                        obesityClass1Row;
                break;

            case "Obesity Class 2":
                selectedRow =
                        obesityClass2Row;
                break;

            case "Obesity Class 3":
                selectedRow =
                        obesityClass3Row;
                break;
        }

        if (selectedRow != null) {

            selectedRow.setAlpha(1f);

            selectedRow.setElevation(
                    getResources()
                            .getDisplayMetrics()
                            .density * 4
            );
        }
    }

    // ============================================================
    // RESET RANGE ROWS
    // ============================================================

    private void resetRangeRows() {

        LinearLayout[] rows = {

                severeThinnessRow,
                moderateThinnessRow,
                mildThinnessRow,
                normalWeightRow,
                overweightRow,
                obesityClass1Row,
                obesityClass2Row,
                obesityClass3Row
        };

        for (LinearLayout row : rows) {

            if (row != null) {

                row.setAlpha(0.55f);

                row.setElevation(0);
            }
        }
    }

    // ============================================================
    // RECALCULATE BUTTON
    // ============================================================

    private void setupRecalculateButton() {

        recalculateButton.setVisibility(
                View.GONE
        );

        recalculateButton.setOnClickListener(
                v -> {

                    // Unlock everything
                    setMeasurementControlsEnabled(
                            true
                    );

                    // Show Calculate
                    calculateButton.setVisibility(
                            View.VISIBLE
                    );

                    // Hide Recalculate
                    recalculateButton.setVisibility(
                            View.GONE
                    );

                    // Clear result
                    resetResult();

                    // Focus currently selected
                    // height input
                    if (heightUnitSpinner
                            .getSelectedItemPosition()
                            == HEIGHT_CM) {

                        heightInput.requestFocus();

                        heightInput.setSelection(
                                heightInput
                                        .getText()
                                        .length()
                        );

                    } else {

                        heightFeetInput.requestFocus();

                        heightFeetInput.setSelection(
                                heightFeetInput
                                        .getText()
                                        .length()
                        );
                    }
                }
        );
    }

    // ============================================================
    // ENABLE / DISABLE MEASUREMENT CONTROLS
    // ============================================================

    private void setMeasurementControlsEnabled(
            boolean enabled) {

        // --------------------------------------------------------
        // Inputs
        // --------------------------------------------------------

        heightInput.setEnabled(enabled);

        heightFeetInput.setEnabled(enabled);

        heightInchesInput.setEnabled(enabled);

        weightInput.setEnabled(enabled);

        // --------------------------------------------------------
        // Buttons
        // --------------------------------------------------------

        heightMinusButton.setEnabled(enabled);

        heightPlusButton.setEnabled(enabled);

        weightMinusButton.setEnabled(enabled);

        weightPlusButton.setEnabled(enabled);

        // --------------------------------------------------------
        // SeekBars
        // --------------------------------------------------------

        heightSeekBar.setEnabled(enabled);

        weightSeekBar.setEnabled(enabled);

        // --------------------------------------------------------
        // Unit selectors
        // --------------------------------------------------------

        heightUnitSpinner.setEnabled(enabled);

        weightUnitSpinner.setEnabled(enabled);

        // --------------------------------------------------------
        // Visual state
        // --------------------------------------------------------

        float alpha =
                enabled
                        ? 1.0f
                        : 0.55f;

        heightInput.setAlpha(alpha);

        heightFeetInput.setAlpha(alpha);

        heightInchesInput.setAlpha(alpha);

        weightInput.setAlpha(alpha);

        heightMinusButton.setAlpha(alpha);

        heightPlusButton.setAlpha(alpha);

        weightMinusButton.setAlpha(alpha);

        weightPlusButton.setAlpha(alpha);

        heightSeekBar.setAlpha(alpha);

        weightSeekBar.setAlpha(alpha);

        heightUnitSpinner.setAlpha(alpha);

        weightUnitSpinner.setAlpha(alpha);

        // --------------------------------------------------------
        // Hide keyboard after locking
        // --------------------------------------------------------

        if (!enabled) {
            hideKeyboard();
        }
    }

    // ============================================================
    // CHECK WHETHER MEASUREMENTS ARE EDITABLE
    // ============================================================

    private boolean areMeasurementsEnabled() {

        return heightInput != null
                && weightInput != null
                && heightInput.isEnabled()
                && weightInput.isEnabled();
    }

    // ============================================================
    // RESET RESULT
    // ============================================================

    private void resetResult() {

        resultCard.setVisibility(
                View.GONE
        );

        interpretationCard.setVisibility(
                View.GONE
        );

        bmiValueText.setText("--");

        bmiCategoryText.setText("");

        bmiStatusText.setText("");

        interpretationText.setText("");

        scaleIndicatorText.setTranslationX(0);

        scaleValueText.setText("");

        resetRangeRows();
    }

    // ============================================================
    // CLAMP FLOAT
    // ============================================================

    private float clamp(
            float value,
            float min,
            float max) {

        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }

    // ============================================================
    // CLAMP INTEGER
    // ============================================================

    private int clampInt(
            int value,
            int min,
            int max) {

        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }

    // ============================================================
    // HIDE KEYBOARD
    // ============================================================

    private void hideKeyboard() {

        View currentFocus =
                getCurrentFocus();

        if (currentFocus != null) {

            InputMethodManager imm =
                    (InputMethodManager)
                            getSystemService(
                                    Context.INPUT_METHOD_SERVICE
                            );

            if (imm != null) {

                imm.hideSoftInputFromWindow(
                        currentFocus.getWindowToken(),
                        0
                );
            }

            currentFocus.clearFocus();
        }
    }
}