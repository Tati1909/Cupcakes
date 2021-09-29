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
package com.example.cupcakes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcakes.databinding.FragmentStartBinding
import com.example.cupcakes.model.OrderViewModel

/**
 * This is the first screen of the Cupcake app. The user can choose how many cupcakes to order.
 */
class StartFragment : Fragment() {

    // Binding object instance corresponding to the fragment_start.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var binding: FragmentStartBinding? = null

    //В Kotlin для каждого var свойства mutable ( ) автоматически создаются функции получения
    // и установки по умолчанию. Функции установки и получения вызываются, когда вы присваиваете значение или
    // читаете значение свойства. (Для свойства val, доступного только для чтения ( ), по умолчанию создается только функция получения.
    // Эта функция получения вызывается, когда вы читаете значение свойства, доступного только для чтения.)
    //Делегирование свойств в Kotlin помогает передать ответственность за геттер-сеттер другому классу.
    //Этот класс (называемый классом делегата ) предоставляет функции получения и установки свойства и обрабатывает его изменения.

    // Использование делегата свойства Kotlin 'by activityViewModels ()' из артефакта fragment-ktx
    //Общая ViewModel. Данную модель мы создаем в каждом фрагменте
    private val sharedViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //привязываем переменную startFragment из макета к экземпляру фрагмента StartFragment
        binding?.startFragment = this
    }

    /**
    Начните заказ с желаемого количества кексов и перейдите к следующему экрану     */
    fun orderCupcake(quantity: Int) {
        // обновить количество, прежде чем переходить к фрагменту аромата
        sharedViewModel.setQuantity(quantity)

        //  установите аромат по умолчанию как Ваниль, если аромат не установлен,
        //  перед переходом к фрагменту аромата.
        if (sharedViewModel.hasNoFlavorSet()) {
            sharedViewModel.setFlavor(getString(R.string.vanilla))
        }

        // Переходим к следующему пункту назначения, чтобы выбрать вкус кексов
        // Добавляем импорт import androidx.navigation.fragment.findNavController
        findNavController().navigate(R.id.action_startFragment_to_flavorFragment)
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}