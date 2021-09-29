/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcakes.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

//Цена одного кекса
private const val PRICE_PER_CUPCAKE = 2.00

//Доплата за получение заказа в тот же день
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

/**
 *[OrderViewModel] содержит информацию о заказе кексов с точки зрения количества, вкуса и
 * подбора даты. Он также знает, как рассчитать общую стоимость на основе этих деталей заказа.
 */
class OrderViewModel : ViewModel() {

    //Количество кексов в заказе
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    // Вкус кексов для заказа
    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    // Возможные варианты даты(4 даты):
    //dateOptions[0]в viewModel(сегодня)
    // dateOptions[1]в viewModel(завтра)
    // dateOptions[2]в viewModel(послезавтра)
    // dateOptions[3] послепослезавтра
    val dateOptions: List<String> = getPickupOptions()

    // Подобрать дату
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    // Цена заказа на данный момент
    // Отображается в каждом макете при выборе вкуса, количества и даты доставки
    private val _price = MutableLiveData<Double>()
    //Делаем цену String, т к будем форматировать ее и возвращать местную валюту($,P,др.)
    val price: LiveData<String> = Transformations.map(_price) {
        // Форматируем цену в местной валюте и возвращаем ее как LiveData <String>
        NumberFormat.getCurrencyInstance().format(it)
    }

    //Часто требуется выполнять первичную инициализацию объекта при его создании. В Java это обычно
    //делается в конструкторе. В Kotlin для этого есть блок Init, который будет выполняться при создании
    //экземпляра OrderViewModel
    init {
        // Устанавливаем начальные значения для заказа
        resetOrder()
    }

    /**
     * Set the quantity of cupcakes for this order.
     *
     * @param numberCupcakes to order
     */
    fun setQuantity(numberCupcakes: Int) {
        _quantity.value = numberCupcakes
        updatePrice()
    }

    //Установите количество кексов для этого заказа
    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }

    //Установите дату получения для этого заказа
    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        //обновляем стоимость в макете в случае выбора заказа на сегодня и добавки на 3 доллара
        updatePrice()
    }

    /**
    Возвращает true, если аромат еще не выбран для заказа. В противном случае возвращает false.     */
    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    /**
    Сбросьте порядок, используя исходные значения по умолчанию для количества, вкуса, даты и цены.
     */
    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    /**
     * Updates the price based on the order details.
     */
    private fun updatePrice() {
        var calculatedPrice = (quantity.value ?: 0) * PRICE_PER_CUPCAKE
// Если пользователь выбрал первый вариант (сегодня) для самовывоза, добавляем надбавку 3 доллара
        if (dateOptions[0] == _date.value) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    /**
    Возвращает список вариантов даты, начиная с текущей даты и следующих 3 дат
     */
    private fun getPickupOptions(): List<String> {

        val options = mutableListOf<String>()
        // В строке шаблона это E означает название дня недели, и выполняется синтаксический анализ
        // как « Вт, 10 декабря »
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        //Получите Calendar экземпляр и назначьте его новой переменной. Сделайте это val. Эта переменная
        // будет содержать текущую дату и время.
        val calendar = Calendar.getInstance()
        //Составьте список дат, начиная с текущей даты и следующих трех дат. Поскольку вам понадобится 4 варианта даты,
        // повторите этот блок кода 4 раза. Этот repeat блок отформатирует дату, добавит ее в список опций даты,
        // а затем увеличит календарь на 1 день.
        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        //Возвращаем обновленное options в конце метода
        return options
    }
}