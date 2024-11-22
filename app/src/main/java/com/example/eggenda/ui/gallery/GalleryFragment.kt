package com.example.eggenda.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.databinding.FragmentGalleryBinding
import com.example.eggenda.gamePetChoose.GamePetChooseViewModel
import com.example.eggenda.gamePetChoose.PetChooseDialogFragment
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.petInfo2
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.input.ObservableInputStream

class GalleryFragment : Fragment() {

    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var petInfo: petInfo2
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var allPetsArrayID : IntArray
    private lateinit var petsAdapter: GalleryAdapter
    private lateinit var characterRecyclerView: RecyclerView

    private var _binding: FragmentGalleryBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.edrqwe
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //inflate the layout for the gallery fragment
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        sharedPreferenceManager = SharedPreferenceManager(requireContext())
        petInfo = petInfo2()

        val binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        characterRecyclerView = binding.galleryCharacterchooseRecyclerView

        characterRecyclerView = root.findViewById(R.id.gallery_characterchoose_recyclerView)
        characterRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

//        val factory = GalleryViewModel.GalleryViewModelFactory(sharedPreferenceManager)
        galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]

        //initialize pets array that has in the code in int array
        //here should take it from pet info class
        allPetsArrayID = intArrayOf(0,1,2,3,4)

        //initalized classes and views
        characterRecyclerView = root.findViewById(R.id.gallery_characterchoose_recyclerView)


        //set the adapter to show the pets
        petsAdapter = GalleryAdapter(
            allPetsArrayID,
            sharedPreferenceManager,
            {petId -> showPetDetailDialog(petId)})

        petsAdapter.notifyDataSetChanged()
        characterRecyclerView.adapter = petsAdapter

        galleryViewModel.allPets.observe(viewLifecycleOwner, Observer { photos ->
            Log.d("Gallery Fragment", "allPets updated: $photos")
            petsAdapter.updatePetsChoose(photos)
        })


        return root
    }

    private fun showPetDetailDialog(petId: Int) {
        // Show a dialog with pet details
        val dialog = PetChooseDialogFragment.newInstance(petId)
        dialog.show(parentFragmentManager, "PetChooseDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}