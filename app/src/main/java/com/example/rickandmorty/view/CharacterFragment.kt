package com.example.rickandmorty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.rickandmorty.databinding.FragmentCharacterBinding
import com.example.rickandmorty.viewmodel.CharacterViewModel
import com.example.rickandmorty.R

private const val ARG_ID = "characterId"
private const val ARG_NAME = "characterName"
private const val ARG_STATUS = "characterStatus"
private const val ARG_SPECIES = "characterSpecies"
private const val ARG_TYPE = "characterType"
private const val ARG_GENDER = "characterGender"
private const val ARG_ORIGIN = "characterOrigin"
private const val ARG_LOCATION = "characterLocation"
private const val ARG_IMAGE = "characterImage"
//private const val ARG_PAGE = "characterPage"

class CharacterFragment : Fragment() {
    private var id: Int? = null
    private var name: String? = null
    private var status: String? = null
    private var species: String? = null
    private var type: String? = null
    private var gender: String? = null
    private var origin: String? = null
    private var location: String? = null
    private var image: String? = null
    private lateinit var binding: FragmentCharacterBinding
    private lateinit var viewModel: CharacterViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ARG_ID)
            name = it.getString(ARG_NAME)
            status = it.getString(ARG_STATUS)
            species = it.getString(ARG_SPECIES)
            type = it.getString(ARG_TYPE)
            gender = it.getString(ARG_GENDER)
            origin = it.getString(ARG_ORIGIN)
            location = it.getString(ARG_LOCATION)
            image = it.getString(ARG_IMAGE)
            //page = it.getInt(ARG_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CharacterViewModel::class)

        setArgumentsContent()
        observeViewModel()

        viewModel.loadEpisodesWithCharacter(id!!)

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            onBackPressedCallback
        )

        setupToolbar()
    }

    private fun observeViewModel() {
        viewModel.episodeList.observe(viewLifecycleOwner) {
            binding.episodes.adapter = EpisodeListAdapter(it)
        }
    }

    private fun setArgumentsContent() {

        Glide.with(requireContext())
            .load(image)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.empty_image)
            .into(binding.image)
        binding.name.text = name
        binding.status.text = status
        binding.species.text = species
        binding.gender.text = gender
        binding.type.text = type
        binding.origin.text = origin
        binding.location.text = location
    }


    private fun setupToolbar() {

        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.title = ""
        binding.toolbar.setNavigationOnClickListener {
            goBack()
        }
    }

    private fun goBack() {
        findNavController().navigate(
            R.id.action_characterFragment_to_characterListFragment
        )
    }
}