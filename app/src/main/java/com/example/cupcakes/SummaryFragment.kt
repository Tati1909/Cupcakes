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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cupcakes.databinding.FragmentSummaryBinding
import com.example.cupcakes.model.OrderViewModel

/**
 * [SummaryFragment] contains a summary of the order details with a button to share the order
 * via another app.
 */
class SummaryFragment : Fragment() {

    private var binding: FragmentSummaryBinding? = null

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSummaryBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            summaryFragment = this@SummaryFragment
        }
    }

    /**
     * Отправьте заказ, поделившись деталями заказа с другим приложением с помощью неявного намерения.
    !!!! Более подробно смотри 3.4.3.5
     * */
    fun sendOrder() {
        //Создаем сводный текст заказа с информацией из ViewModel.
        //Проще всего сначала вычислить количество из ViewModel и сохранить его в переменной.
        // Поскольку quantity имеет тип LiveData<Int>,то sharedViewModel.quantity.value может быть null.
        // Если он равен нулю, используем 0 в качестве значения по умолчанию для numberOfCupcakes.
        val numberOfCupcakes = sharedViewModel.quantity.value ?: 0
        // Например:
        //Quantity: 12 cupcakes
        //Flavor: Chocolate
        //Pickup date: Sat Dec 12
        //Total: $24.00
        //Thank you!
        val orderSummary = getString(
            R.string.order_details,
            //plurals для ед. и множ. числа: капкейк и капкейки
            resources.getQuantityString(R.plurals.cupcakes, numberOfCupcakes, numberOfCupcakes),
            sharedViewModel.flavor.value.toString(),
            sharedViewModel.date.value.toString(),
            sharedViewModel.price.value.toString()
        )

        // Создаем неявное намерение ACTION_SEND с деталями заказа в дополнительных функциях намерения
        val intent = Intent(Intent.ACTION_SEND)
            //тип(текст, простой)
            .setType("text/plain")
            //тема сообщения: Новый заказ капкейков
            .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.new_cupcake_order))
            //тело сообщения
            .putExtra(Intent.EXTRA_TEXT, orderSummary)

        //Проверяем, есть ли приложение, которое может обработать это намерение, перед его запуском
        if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
            // Запуск Activity с заданным намерением (это может открыть диалоговое окно общего доступа на
            // устройство, если несколько приложений могут обрабатывать это намерение)
            startActivity(intent)
        }
    }

    /**
     * Cancel the order and start over.
     */
    fun cancelOrder() {
        // Reset order in view model
        sharedViewModel.resetOrder()

        // Navigate back to the [StartFragment] to start over
        findNavController().navigate(R.id.action_summaryFragment_to_startFragment)
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