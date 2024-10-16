package com.saefulrdevs.dicodingevent.ui.upcoming_event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.dicodingevent.R
import com.saefulrdevs.dicodingevent.data.remote.response.ListEventsItem
import com.saefulrdevs.dicodingevent.databinding.FragmentUpcomingEventBinding
import com.saefulrdevs.dicodingevent.utils.UiHandler.handleError
import com.saefulrdevs.dicodingevent.viewmodel.AdapterVerticalEvent
import com.saefulrdevs.dicodingevent.viewmodel.MainViewModel
import com.saefulrdevs.dicodingevent.viewmodel.ViewModelFactory

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterVertical: AdapterVerticalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupAdapter()
        observeViewModel()

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupRecyclerView() {
        binding?.apply {
            val verticalLayout = LinearLayoutManager(requireContext())
            rvUpcomingEvent.layoutManager = verticalLayout
            val itemUpcomingEventDecoration =
                DividerItemDecoration(requireContext(), verticalLayout.orientation)
            rvUpcomingEvent.addItemDecoration(itemUpcomingEventDecoration)
        }
    }

    private fun setupAdapter() {
        adapterVertical = AdapterVerticalEvent { eventId ->
            val bundle = Bundle().apply {
                eventId?.let { putInt("eventId", it) }
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        binding?.rvUpcomingEvent?.adapter = adapterVertical
    }

    private fun observeViewModel() {
        binding?.apply {
            mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { listItems ->
                setUpcomingEvent(listItems)
                mainViewModel.clearErrorMessage()
            }

            mainViewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it, progressBar)
            }

            mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                handleError(
                    isError = errorMessage != null,
                    message = errorMessage,
                    errorTextView = tvErrorMessage,
                    refreshButton = btnRefresh,
                    recyclerView = rvUpcomingEvent
                ) {
                    mainViewModel.getUpcomingEvent()
                }
            }
        }
    }

    private fun setUpcomingEvent(listUpcomingEvent: List<ListEventsItem>) {
        adapterVertical.submitList(listUpcomingEvent)
    }

    // Fungsi untuk menampilkan atau menyembunyikan ProgressBar
    private fun showLoading(isLoading: Boolean, progressBar: ProgressBar) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
