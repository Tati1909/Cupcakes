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
import com.example.cupcakes.databinding.FragmentFlavorBinding
import com.example.cupcakes.model.OrderViewModel

/**
 * [FlavorFragment] allows a user to choose a cupcake flavor for the order.
 */
class FlavorFragment : Fragment() {

    private var binding: FragmentFlavorBinding? = null

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentFlavorBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // Указываем фрагмент как владельца жизненного цикла (lifecycle owner) для правильного
            // обновления цены в макетах. Т. к. цена является Livedata, а фрагменты - владельцами жизненного цикла (lifecycle owner).
            lifecycleOwner = viewLifecycleOwner

            //Дополнительная информация о привязке данных:
            //Напомним, что библиотека привязки данных является частью Android Jetpack .
            // Привязка данных связывает компоненты пользовательского интерфейса в ваших макетах с источниками данных в вашем приложении
            // с использованием декларативного формата. Проще говоря, привязка данных - это привязка данных (из кода) к View +
            // привязка View  к коду. Благодаря настройке этих привязок и автоматическому обновлению
            // это помогает снизить вероятность ошибок, если вы забудете вручную обновить пользовательский интерфейс из своего кода.

            // Присваиваем общую ViewModel к viewModel из макета
            viewModel = sharedViewModel

            //привязываем переменную flavorFragment из макета к экземпляру фрагмента FlavorFragment для выбора
            // нужного количества капкейков и перехода на другой экран
            flavorFragment = this@FlavorFragment
        }
    }

    //Перейдите к следующему экрану, чтобы выбрать дату получения.     */
    fun goToNextScreen() {
        findNavController().navigate(R.id.action_flavorFragment_to_pickupFragment)
    }

    /**
     * Cancel the order and start over.
     */
    fun cancelOrder() {
        // Reset order in view model
        sharedViewModel.resetOrder()

        // Navigate back to the [StartFragment] to start over
        findNavController().navigate(R.id.action_flavorFragment_to_startFragment)
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