package com.caoimhin.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel(){
    // Variables to hold the operation and type of calculation
    private var operand1: Double? = null
    private var pendingOperation = "="

    private val result  = MutableLiveData<Double>()
    val stringResult: LiveData<String>
        get() = Transformations.map(result) {it?.toString()}

    private val newNumber = MutableLiveData<String>()
    val stringNewNumber: LiveData<String>
        get() = newNumber

    private val operation = MutableLiveData<String>()
    val stringOperation: LiveData<String>
        get() = operation

    fun digitPressed(caption: String){
        if(newNumber.value != null){
            newNumber.value = newNumber.value + caption
        }else{
            newNumber.value = caption
        }
    }

    fun operandPressed(op: String){
        try {
            val value = newNumber.value?.toDouble()
            if(value != null){
                performOperation(value, op)
            }
        } catch (e: NumberFormatException) {
            newNumber.value = ""
        }

        pendingOperation = op
        operation.value = pendingOperation
    }

    fun negPressed(){
        val value = newNumber.value
        if(value == null || value.isEmpty()){
            newNumber.value = "-"
        }else{
            try {
                var doubleValue = value.toDouble()
                doubleValue *= -1
                newNumber.value = doubleValue.toString()
            }catch (e: NumberFormatException){
                // newNumber was "-" or ".", so clear it
                newNumber.value = ""
            }
        }
    }

    fun delPressed(){
        newNumber.value = null
        result.value = null
        operation.value = null
        operand1 = null
    }

    private fun performOperation(value: Double, operation: String) {
        if (operand1 == null) {
            operand1 = value
        } else {
            if (pendingOperation == "=") {
                pendingOperation = operation
            }

            when (pendingOperation) {
                "=" -> operand1 = value
                "/" -> operand1 = if (operand1 == 0.0) {
                    Double.NaN   // handle attempt to divide by zero
                } else {
                    operand1!! / value
                }
                "*" -> operand1 = operand1!! * value
                "-" -> operand1 = operand1!! - value
                "+" -> operand1 = operand1!! + value
            }
        }

        result.value = operand1
        newNumber.value = ""
    }
}