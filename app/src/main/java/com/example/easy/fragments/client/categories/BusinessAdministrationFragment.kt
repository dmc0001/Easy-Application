package com.example.easy.fragments.client.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.easy.data.Category
import com.example.easy.utils.Resource
import com.example.easy.viewmodels.BaseCategoryViewModel
import com.example.easy.viewmodels.providerfactory.BaseCategoryProviderFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BusinessAdministrationFragment : BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    private val businessAdministrationViewModel by viewModels<BaseCategoryViewModel> {
        BaseCategoryProviderFactory(firestore, Category.BusinessAdministration)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                businessAdministrationViewModel.specialJobs.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            Log.d("debugging", resource.data.toString())
                            jobsInfoAdapter.differ.submitList(resource.data)
                            hideLoading()
                        }

                        is Resource.Failed -> {
                            Log.d("debugging", resource.message.toString())
                            Snackbar.make(view, resource.message.toString(), Snackbar.LENGTH_LONG)
                                .show()
                        }

                        else -> Unit
                    }
                }
            }
        }

    }


}