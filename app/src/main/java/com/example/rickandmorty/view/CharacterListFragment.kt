package com.example.rickandmorty.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentCharacterListBinding
import com.example.rickandmorty.viewmodel.CharacterListViewModel
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.rickandmorty.model.db.LocalCharacter
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager


class CharacterListFragment : Fragment() {
    private var currentPageId: Int = 1
    private lateinit var binding: FragmentCharacterListBinding
    private val viewModel: CharacterListViewModel by activityViewModels()
    private var filtersApplied: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        currentPageId = sharedPreferences.getInt("currentPageId", 1)

        observeViewModel()
        setListeners()

        showLoad()
        loadFiltersFromSharedPrefs()
        loadCharactersList()
    }

    private fun loadCharactersList() {
        viewModel.loadCharactersOnPage(
            pageId = currentPageId,
            name = binding.searchEt.text.toString(),
            statuses = listOf(
                binding.aliveStatusCB.isChecked,
                binding.deadStatusCB.isChecked,
                binding.unknownStatusCB.isChecked
            ),
            species = binding.speciesFilterET.text.toString(),
            type = binding.typeFilterET.text.toString(),
            genders = listOf(
                binding.maleGenderCB.isChecked,
                binding.femaleGenderCB.isChecked,
                binding.genderlessGenderCB.isChecked,
                binding.unknownGenderCB.isChecked
            )
        )
    }

    private fun observeViewModel() {
        viewModel.characterList.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = false
            setPaginationButtons()
            hideLoad()
            if (it.isNotEmpty()) {
                binding.characterList.post {
                    binding.characterList.layoutManager?.scrollToPosition(viewModel.scrollPosition)
                }
                binding.characterList.adapter = CharacterListAdapter(it, ::openCharacterPage)
            } else {
                hideLoadWithError()
            }
        }
    }

    private fun setListeners() {
        binding.buttonLeft.setOnClickListener { onLeftButtonClick() }
        binding.buttonRight.setOnClickListener { onRightButtonClick() }
        binding.swipeRefresh.setOnRefreshListener { refreshData() }
        binding.filterButton.setOnClickListener { onFilterButtonClick() }
        binding.confirmFiltersButton.setOnClickListener { onConfirmFilterButtonClick() }
        binding.clearFiltersButton.setOnClickListener { onClearFilterButtonClick() }
        binding.findButton.setOnClickListener { onFindButtonClick() }
        binding.searchEt.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onFindButtonClick()
                true
            } else {
                false
            }
        }
    }

    private fun refreshData() {
        showLoad()
        filtersApplied = false
        viewModel.updateCharacterListOnPage()
    }


    private fun onRightButtonClick() {
        if (viewModel.currentPage.next != null) {
            if (viewModel.currentPage.prev == null) {
                binding.buttonLeft.setBackgroundResource(R.drawable.rectangle_left_rounded_white)
                binding.buttonLeft.setImageResource(R.drawable.left_arrow_white)

                binding.paginationPageLeft.setBackgroundResource(R.drawable.white_stroke_rectangle)
                binding.paginationPageLeft.setTextColor(
                    resources.getColor(
                        R.color.darker_white,
                        null
                    )
                )

                binding.paginationPageCenter.setBackgroundResource(R.drawable.white_rectangle)
                binding.paginationPageCenter.setTextColor(
                    resources.getColor(
                        R.color.black,
                        null
                    )
                )
            }
            currentPageId++
            viewModel.scrollPosition = 0
            showLoad()
            loadCharactersList()
        }
    }

    private fun onLeftButtonClick() {
        if (viewModel.currentPage.prev != null) {
            if (viewModel.currentPage.next == null) {
                binding.buttonRight.setBackgroundResource(R.drawable.rectangle_right_rounded_white)
                binding.buttonRight.setImageResource(R.drawable.right_arrow_white)

                binding.paginationPageRight.setBackgroundResource(R.drawable.white_stroke_rectangle)
                binding.paginationPageRight.setTextColor(
                    resources.getColor(
                        R.color.darker_white,
                        null
                    )
                )

                binding.paginationPageCenter.setBackgroundResource(R.drawable.white_rectangle)
                binding.paginationPageCenter.setTextColor(
                    resources.getColor(
                        R.color.black,
                        null
                    )
                )
            }
            currentPageId--
            viewModel.scrollPosition = 0
            showLoad()
            loadCharactersList()
        }
    }

    private fun setPaginationButtons() {
        Log.w("viewModel.currentPage", viewModel.currentPage.toString())
        if (viewModel.currentPage.prev == null) {
            binding.buttonLeft.setBackgroundResource(R.drawable.rectangle_left_rounded_gray)
            binding.buttonLeft.setImageResource(R.drawable.left_arrow_gray)

            binding.paginationPageLeft.setBackgroundResource(R.drawable.white_rectangle)
            binding.paginationPageLeft.setTextColor(
                resources.getColor(
                    R.color.black,
                    null
                )
            )
            binding.paginationPageLeft.text = viewModel.currentPage.id.toString()

            binding.paginationPageCenter.setBackgroundResource(R.drawable.white_stroke_rectangle)
            binding.paginationPageCenter.setTextColor(
                resources.getColor(
                    R.color.darker_white,
                    null
                )
            )
            binding.paginationPageCenter.text = viewModel.currentPage.next!!.toString()

            binding.paginationPageRight.text = (viewModel.currentPage.next!! + 1).toString()
        } else if (viewModel.currentPage.next == null) {
            binding.buttonRight.setBackgroundResource(R.drawable.rectangle_right_rounded_gray)
            binding.buttonRight.setImageResource(R.drawable.right_arrow_gray)

            binding.paginationPageRight.setBackgroundResource(R.drawable.white_rectangle)
            binding.paginationPageRight.setTextColor(
                resources.getColor(
                    R.color.black,
                    null
                )
            )
            binding.paginationPageRight.text = viewModel.currentPage.id.toString()

            binding.paginationPageCenter.setBackgroundResource(R.drawable.white_stroke_rectangle)
            binding.paginationPageCenter.setTextColor(
                resources.getColor(
                    R.color.darker_white,
                    null
                )
            )
            binding.paginationPageCenter.text = viewModel.currentPage.prev!!.toString()

            binding.paginationPageLeft.text = (viewModel.currentPage.prev!! - 1).toString()
        } else {
            binding.paginationPageLeft.text = viewModel.currentPage.prev!!.toString()
            binding.paginationPageCenter.text = viewModel.currentPage.id.toString()
            binding.paginationPageRight.text = viewModel.currentPage.next!!.toString()
            binding.buttonLeft.setBackgroundResource(R.drawable.rectangle_left_rounded_white)
            binding.buttonLeft.setImageResource(R.drawable.left_arrow_white)

            binding.paginationPageLeft.setBackgroundResource(R.drawable.white_stroke_rectangle)
            binding.paginationPageLeft.setTextColor(
                resources.getColor(
                    R.color.darker_white,
                    null
                )
            )

            binding.paginationPageCenter.setBackgroundResource(R.drawable.white_rectangle)
            binding.paginationPageCenter.setTextColor(
                resources.getColor(
                    R.color.black,
                    null
                )
            )

            binding.buttonRight.setBackgroundResource(R.drawable.rectangle_right_rounded_white)
            binding.buttonRight.setImageResource(R.drawable.right_arrow_white)

            binding.paginationPageRight.setBackgroundResource(R.drawable.white_stroke_rectangle)
            binding.paginationPageRight.setTextColor(
                resources.getColor(
                    R.color.darker_white,
                    null
                )
            )
        }

    }

    private fun showLoad() {
        binding.progressBar.visibility = View.VISIBLE
        binding.characterList.visibility = View.INVISIBLE
        binding.loadingErrorTv.visibility = View.GONE
        blockButtons()
    }

    private fun hideLoad() {
        binding.progressBar.visibility = View.GONE
        binding.characterList.visibility = View.VISIBLE
        unblockButtons()
    }

    private fun hideLoadWithError() {
        binding.progressBar.visibility = View.GONE
        binding.characterList.visibility = View.INVISIBLE
        binding.loadingErrorTv.visibility = View.VISIBLE
        if (filtersApplied) {
            binding.loadingErrorTv.setText(R.string.empty_filtered_list_message)
        } else {
            binding.loadingErrorTv.setText(R.string.loading_error)
            binding.buttonLeft.isEnabled = true
            binding.buttonRight.isEnabled = false
            binding.findButton.isEnabled = false
            binding.filterButton.isEnabled = false
            binding.searchEt.isEnabled = false
        }
    }

    private fun blockButtons() {
        binding.buttonLeft.isEnabled = false
        binding.buttonRight.isEnabled = false
        binding.findButton.isEnabled = false
        binding.filterButton.isEnabled = false
        binding.searchEt.isEnabled = false
    }

    private fun unblockButtons() {
        binding.buttonLeft.isEnabled = true
        binding.buttonRight.isEnabled = true
        binding.findButton.isEnabled = true
        binding.filterButton.isEnabled = true
        binding.searchEt.isEnabled = true
    }

    private fun onFilterButtonClick() {
        if (binding.swipeRefresh.isVisible) {
            binding.filterButton.setBackgroundResource(R.drawable.filter_filled)
            binding.swipeRefresh.visibility = View.GONE
            binding.filterPage.visibility = View.VISIBLE
            blockButtons()
            binding.filterButton.isEnabled = true
        } else {
            binding.filterButton.setBackgroundResource(R.drawable.filter_empty)
            binding.swipeRefresh.visibility = View.VISIBLE
            binding.filterPage.visibility = View.GONE
            unblockButtons()
        }
    }

    private fun onConfirmFilterButtonClick() {
        binding.filterButton.setBackgroundResource(R.drawable.filter_empty)
        binding.swipeRefresh.visibility = View.VISIBLE
        binding.filterPage.visibility = View.GONE
        showLoad()
        filtersApplied = true
        sharedPreferences.edit { putBoolean("aliveStatusCB", binding.aliveStatusCB.isChecked) }
        sharedPreferences.edit { putBoolean("deadStatusCB", binding.deadStatusCB.isChecked) }
        sharedPreferences.edit { putBoolean("unknownStatusCB", binding.unknownStatusCB.isChecked) }
        sharedPreferences.edit { putString("speciesFilterET",binding.speciesFilterET.text.toString()) }
        sharedPreferences.edit { putString("typeFilterET", binding.typeFilterET.text.toString()) }
        sharedPreferences.edit { putBoolean("male_gender", binding.maleGenderCB.isChecked) }
        sharedPreferences.edit { putBoolean("femaleGenderCB", binding.femaleGenderCB.isChecked) }
        sharedPreferences.edit { putBoolean("genderlessGenderCB", binding.genderlessGenderCB.isChecked) }
        sharedPreferences.edit { putBoolean("unknownGenderCB", binding.unknownGenderCB.isChecked) }
        loadCharactersList()
    }

    private fun onClearFilterButtonClick() {
        filtersApplied = false
        binding.aliveStatusCB.isChecked = false
        binding.deadStatusCB.isChecked = false
        binding.unknownStatusCB.isChecked = false
        binding.speciesFilterET.setText("")
        binding.typeFilterET.setText("")
        binding.maleGenderCB.isChecked = false
        binding.femaleGenderCB.isChecked = false
        binding.genderlessGenderCB.isChecked = false
        binding.unknownGenderCB.isChecked = false
    }

    private fun onFindButtonClick() {
        if (binding.searchEt.text.isEmpty()) {
            filtersApplied = false
        } else {
            filtersApplied = true
        }
        sharedPreferences.edit {
            putString(
                "characterNameFilter",
                binding.searchEt.text.toString()
            )
        }
        showLoad()
        loadCharactersList()
    }

    private fun openCharacterPage(character: LocalCharacter) {
        val bundle: Bundle = bundleOf(
            "characterId" to character.id,
            "characterName" to character.name,
            "characterStatus" to character.status,
            "characterSpecies" to character.species,
            "characterType" to character.type,
            "characterGender" to character.gender,
            "characterOrigin" to character.origin,
            "characterLocation" to character.location,
            "characterImage" to character.image
        )
        sharedPreferences.edit { putInt("currentPageId", currentPageId) }
        viewModel.scrollPosition = (binding.characterList.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
        findNavController().navigate(R.id.action_characterListFragment_to_characterFragment, bundle)
    }

    private fun loadFiltersFromSharedPrefs() {
        binding.aliveStatusCB.isChecked = sharedPreferences.getBoolean("aliveStatusCB", false)
        binding.deadStatusCB.isChecked = sharedPreferences.getBoolean("deadStatusCB", false)
        binding.unknownStatusCB.isChecked = sharedPreferences.getBoolean("unknownStatusCB", false)
        binding.speciesFilterET.setText(sharedPreferences.getString("speciesFilterET", ""))
        binding.typeFilterET.setText(sharedPreferences.getString("typeFilterET", ""))
        binding.maleGenderCB.isChecked = sharedPreferences.getBoolean("maleGenderCB", false)
        binding.femaleGenderCB.isChecked = sharedPreferences.getBoolean("femaleGenderCB", false)
        binding.genderlessGenderCB.isChecked =
            sharedPreferences.getBoolean("genderlessGenderCB", false)
        binding.unknownGenderCB.isChecked = sharedPreferences.getBoolean("unknownGenderCB", false)
    }
}