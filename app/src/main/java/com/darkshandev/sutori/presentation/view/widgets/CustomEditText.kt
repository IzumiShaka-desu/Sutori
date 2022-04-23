package com.darkshandev.sutori.presentation.view.widgets

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import com.darkshandev.sutori.R
import com.darkshandev.sutori.utils.AlertUtils
import com.darkshandev.sutori.utils.ValidateFormsUtils
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import java.util.regex.Pattern

class CustomEditText : AppCompatEditText, TextWatcher {
    private var mFormatType: TextValidationType = TextValidationType.defaulttype
    private var customLocale: Locale = Locale.getDefault()

    var isAutoValidateEnable = true
    var isShowMessageError = true
    private var mFirstime = true

    private var mCurrentString = ""
    var maxMount = 0.0
    var minMount = 0.0
    var regularExpression: String? = null

    var emptyMessage: String? = null
    var errorMessage: String? = null

    @DrawableRes
    private var drawableOptions: Int = R.drawable.ic_baseline_expand_more_24
    private var options: Array<String?>? = null
    private var mAutoValidate: CustomEditText.OnValidationListener? = null

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TextValidationEditText,
                0, 0
            )
            try {
                mFormatType = TextValidationType.fromId(
                    typedArray.getInt(
                        R.styleable.TextValidationEditText_format,
                        -11
                    )
                )
                isAutoValidateEnable =
                    typedArray.getBoolean(R.styleable.TextValidationEditText_autoValidate, false)
                isShowMessageError =
                    typedArray.getBoolean(
                        R.styleable.TextValidationEditText_showErrorMessage,
                        false
                    )
                emptyMessage =
                    typedArray.getString(R.styleable.TextValidationEditText_errorEmptyMessage)
                errorMessage = typedArray.getString(R.styleable.TextValidationEditText_errorMessage)
                regularExpression =
                    typedArray.getString(R.styleable.TextValidationEditText_regularExpression)
                minMount =
                    typedArray.getFloat(R.styleable.TextValidationEditText_minAmount, 0f).toDouble()
                maxMount =
                    typedArray.getFloat(R.styleable.TextValidationEditText_maxAmount, 0f).toDouble()
                drawableOptions = typedArray.getResourceId(
                    R.styleable.TextValidationEditText_drawableOptions,
                    R.drawable.ic_baseline_expand_more_24
                )
                try {
                    val id = typedArray.getResourceId(R.styleable.TextValidationEditText_options, 0)
                    if (id != 0) {
                        options = resources.getStringArray(id)
                    }
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
                mFormatType = TextValidationType.defaulttype
            } finally {
                typedArray.recycle()
            }
        }
        super.addTextChangedListener(this)
        configureType(mFormatType)
        if (options != null && options!!.isNotEmpty()) {
            setPickerOptions(options, null)
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    fun setDrawableOptions(drawableOptions: Int) {
        this.drawableOptions = drawableOptions
        setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableOptions, 0)
    }

    fun removeDrawableOptions() {
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    }

    fun setPickerOptions(options: Array<String?>?, listener: CustomEditText.OptionsListener?) {
        if (options == null) {
            return
        }
        enablePickerMode {
            AlertUtils.showPickerDialg(
                context,
                hint.toString(),
                options,
                DialogInterface.OnClickListener { dialog, which ->
                    setText(options[which])
                    listener?.onOptionSelected(this@CustomEditText, options[which])
                }
            )
        }
    }

    fun enablePickerMode(listener: OnClickListener?) {
        setOnClickListener(listener)
        isLongClickable = false
        isClickable = true
        isFocusable = false
        inputType = InputType.TYPE_NULL
        isCursorVisible = false
        setDrawableOptions(drawableOptions)
    }

    private fun configureType(mFormatType: TextValidationType?) {
        if (mFormatType != null) {
            if (mFormatType != TextValidationType.defaulttype) {
                when (mFormatType) {
                    TextValidationType.email -> {
                        this.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        this.maxLines = 1
                    }


                    TextValidationType.text -> {
                        this.maxLines = 1
                        this.inputType = InputType.TYPE_CLASS_TEXT
                    }

                    TextValidationType.phone -> {
                        this.maxLines = 1
                        this.inputType = InputType.TYPE_CLASS_PHONE
                    }

                    TextValidationType.personName -> {
                        this.maxLines = 1
                        this.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    }
                    TextValidationType.password -> {
                        this.maxLines = 1
                        val cache: Typeface = this.typeface
                        this.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        this.setTypeface(cache)
                    }
                }
            }
        }
    }

    fun disablePickerMode() {
        setOnClickListener(null)
        isLongClickable = true
        isClickable = false
        isFocusable = true
        isCursorVisible = true
        removeDrawableOptions()
        configureType(mFormatType)
    }

    interface OptionsListener {
        fun onOptionSelected(editText: CustomEditText?, option: String?)
    }

    interface OnValidationListener {
        fun onValidEditText(editText: CustomEditText?, text: String?)
        fun onInvalidEditText(editText: CustomEditText?)
    }

    fun setOnValidationListener(mAutoValidate: OnValidationListener?) {
        this.mAutoValidate = mAutoValidate
    }

    fun getCustomLocale(): Locale {
        return customLocale
    }

    fun setCustomLocale(customLocale: Locale) {
        this.customLocale = customLocale
    }


    var formatType: TextValidationType
        get() = mFormatType
        set(mFormatType) {
            this.mFormatType = mFormatType
            configureType(mFormatType)
        }

    fun getDrawableOptions(): Int {
        return drawableOptions
    } //endregion

    val isValidField: Boolean
        get() = validateEditText(mFormatType, mCurrentString)

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (mFormatType != TextValidationType.defaulttype) {
            removeTextChangedListener(this)
            val currentString = s.toString()
            mCurrentString = currentString

            if (isAutoValidateEnable && !mFirstime) {
                validateEditText(mFormatType, mCurrentString)
            }
            addTextChangedListener(this)
            mFirstime = false
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    private fun validateEditText(mFormatType: TextValidationType, currentString: String): Boolean {
        var validField = true
        var errorMessage: String? = null
        if (TextUtils.isEmpty(currentString.trim { it <= ' ' })) {
            errorMessage =
                if (emptyMessage != null) emptyMessage else context.getString(R.string.msg_empty_edittext)
            validField = false
        } else if (regularExpression != null) {
            val customizePattern: Pattern = Pattern.compile(regularExpression)
            validField = customizePattern.matcher(currentString).matches()
            if (!validField) {
                errorMessage =
                    if (this.errorMessage != null) this.errorMessage else context.getString(R.string.msg_invalid_edittext)
            }
        } else {
            when (mFormatType) {
                TextValidationType.email -> {
                    validField = ValidateFormsUtils.isValidEmailAddress(currentString)
                    if (!validField) {
                        errorMessage =
                            if (this.errorMessage != null) this.errorMessage else context.getString(
                                R.string.msg_invalid_edittext
                            )
                    }
                }
                TextValidationType.password -> {
                    validField = ValidateFormsUtils.isValidPassword(currentString)
                    if (!validField) {
                        errorMessage =
                            if (this.errorMessage != null) this.errorMessage else context.getString(
                                R.string.msg_invalid_edittext
                            )
                    }
                }
            }
        }
        if (mAutoValidate != null) {
            if (validField) {
                mAutoValidate!!.onValidEditText(this@CustomEditText, mCurrentString)
            } else {
                mAutoValidate!!.onInvalidEditText(this@CustomEditText)
            }
        }
        if (isShowMessageError) {
            setErrorTextInputLayout(errorMessage)
        }
        return validField
    }

    val inputLayoutContainer: TextInputLayout?
        get() {
            if (this.parent != null) {
                if (this.parent is TextInputLayout) {
                    return this.parent as TextInputLayout
                } else {
                    if (this.parent.parent != null && this.parent.parent is TextInputLayout) {
                        return this.parent.parent as TextInputLayout
                    }
                }
            }
            return null
        }

    private fun setErrorTextInputLayout(errorMessage: String?) {
        if (inputLayoutContainer != null) {
            inputLayoutContainer!!.error = errorMessage
        } else {
            super.setError(errorMessage)
        }
    }

    private fun setErrorTextInputLayout(errorMessage: CharSequence) {
        if (inputLayoutContainer != null) {
            inputLayoutContainer!!.error = errorMessage
        } else {
            super.setError(errorMessage)
        }
    }

    override fun setError(error: CharSequence) {
        setErrorTextInputLayout(error)
    }


}

enum class TextValidationType(var id: Int) {
    defaulttype(-11),
    email(0),
    password(1),
    phone(2),
    text(4),
    number(5),
    personName(8);


    companion object {
        fun fromId(id: Int): TextValidationType {
            try {
                for (b in values()) {
                    if (b.id == id) {
                        return b
                    }
                }
            } catch (e: java.lang.Exception) {
                return defaulttype
            }
            return defaulttype
        }
    }
}