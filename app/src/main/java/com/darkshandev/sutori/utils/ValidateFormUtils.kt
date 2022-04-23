package com.darkshandev.sutori.utils

import android.util.Patterns
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

object ValidateFormsUtils {
    private val emptyString: Pattern = Pattern.compile("^(?=\\s*\\S).*$")
    private val ptVisa: Pattern = Pattern.compile("^4[0-9]{6,}$")
    private val ptMasterCard: Pattern = Pattern.compile("^5[1-5][0-9]{5,}$")
    private val ptAmeExp: Pattern = Pattern.compile("^3[47][0-9]{5,}$")
    private val ptDinClb: Pattern = Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{4,}$")
    private val ptDiscover: Pattern = Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{3,}$")
    private val ptJcb: Pattern = Pattern.compile("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$")
    private val genericCreditCard: Pattern = Pattern.compile("^[0-9]{16}$")
    private val cablePtrn: Pattern = Pattern.compile("^[0-9]{18}$")
    private val phonePattern: Pattern = Pattern.compile("^[0-9]{8}|[0-9]{10}$")
    private val cellPhonePattern: Pattern = Pattern.compile("^[0-9]{10}$")
    private val number: Pattern = Pattern.compile("[0-9]+")

    /**
     * ^           # Assert position at the beginning of the string.
     * [0-9]{5}    # Match a digit, exactly five times.
     * (?:         # Group but don't capture:
     * -         #   Match a literal "-".
     * [0-9]{4}  #   Match a digit, exactly four times.
     * )           # End the non-capturing group.
     * ?         #   Make the group optional.
     * $           # Assert position at the end of the string.
     */
    private val zipCodePattern: Pattern = Pattern.compile("^[0-9]{5}$")

    /*
   *   (			# Start of group
       (?=.*\d)		#   must contains one digit from 0-9
       (?=.*[a-z])		#   must contains one lowercase characters
       (?=.*[A-Z])		#   must contains one uppercase characters
             .		    #     match anything with previous condition checking
               {6,20}	#        length at least 6 characters and maximum of 20
           )			# End of group
   * */
    private val passwordPattern: Pattern =
        Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})")
    private val emailPattern: Pattern =
        Patterns.EMAIL_ADDRESS //Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

    fun isValidEmailAddress(email: String?): Boolean {
        return emailPattern.matcher(email).matches()
    }

    fun isValidPassword(pass: String): Boolean {
        return pass.isNotEmpty() && pass.length >= 6 //passwordPattern.matcher(pass).matches();
    }

    fun isValidPhone(phone: String?): Boolean {
        return phonePattern.matcher(phone).matches()
    }

    fun isValidCellPhone(cellPhone: String?): Boolean {
        return cellPhonePattern.matcher(cellPhone).matches()
    }

    fun isValidNumber(compare: String?): Boolean {
        return number.matcher(compare).matches()
    }

    fun isValidZipCode(zipCode: String?): Boolean {
        return zipCodePattern.matcher(zipCode).matches()
    }

    fun isValidCreditCard(card: String?): Boolean {
        return genericCreditCard.matcher(card).matches()
    }

    fun isValidCABLE(cable: String?): Boolean {
        return cablePtrn.matcher(cable).matches()
    }
}

object ValidationUtils {
    fun cashFormat(locale: Locale?, number: Double): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        return try {
            format.format(number)
        } catch (e: java.lang.Exception) {
            number.toString()
        }
    }

    fun cashFormat(locale: Locale?, number: String): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        return try {
            val cast = java.lang.Double.valueOf(number)
            format.format(cast)
        } catch (e: java.lang.Exception) {
            number
        }
    }

    fun cashFormat(locale: Locale?, number: Int): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        return try {
            format.format(number)
        } catch (e: java.lang.Exception) {
            number.toString()
        }
    }

    fun cashFormat(locale: Locale?, number: Float): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        return try {
            format.format(number)
        } catch (e: java.lang.Exception) {
            number.toString()
        }
    }

    fun parseCurrencyAmount(aMount: String): Double {
        return try {
            val cleanString = aMount.replace("[^\\d.]".toRegex(), "").replace("\\.".toRegex(), "")
            cleanString.toDouble()
        } catch (e: java.lang.Exception) {
            0.0
        }
    }

    fun parseCurrencyAmountWithoutDecimal(aMount: String): Double {
        return try {
            val cleanString = aMount.replace("[^\\d.]".toRegex(), "")
            val splitedAmount = cleanString.split("\\.").toTypedArray()
            if (splitedAmount.size == 2) {
                var mount = splitedAmount[0]
                val decimals = splitedAmount[1]
                if (decimals.length == 1) {
                    mount = splitedAmount[0].replaceFirst(".$".toRegex(), "")
                }
                val cleanDecimals =
                    decimals.replaceFirst("0".toRegex(), "").replaceFirst("0".toRegex(), "")
                return (mount + cleanDecimals).toDouble()
            }
            cleanString.toDouble()
        } catch (e: java.lang.Exception) {
            0.0
        }
    }

    fun parseCurrencyAmountString(aMount: String): String {
        return try {
            val doubleMount = parseCurrencyAmount(aMount) / 100
            doubleMount.toString()
        } catch (e: java.lang.Exception) {
            aMount
        }
    }
}