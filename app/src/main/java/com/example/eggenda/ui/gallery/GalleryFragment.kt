package com.example.eggenda.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
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

        //the mutuable list that can save the list of the pets ,that can send to the game part
        val selectedPetID =  MutableLiveData<Int?>()

//        val factory = GalleryViewModel.GalleryViewModelFactory(sharedPreferenceManager)
        galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]

        //initialize pets array that has in the code in int array
        //here should take it from pet info class
        allPetsArrayID = intArrayOf(0,1,2,3,4,5,6,7,8,9)

        //initalized classes and views
        characterRecyclerView = root.findViewById(R.id.gallery_characterchoose_recyclerView)

        //set the adapter to show the pets
        petsAdapter = GalleryAdapter(
            allPetsArrayID,
            sharedPreferenceManager,
            {petId -> galleryViewModel.selectPet(petId)},
            {petId -> showPetDetailDialog(petId)})

        petsAdapter.notifyDataSetChanged()
        characterRecyclerView.adapter = petsAdapter

        galleryViewModel.allPets.observe(viewLifecycleOwner, Observer { photos ->
            Log.d("Gallery Fragment", "allPets updated: $photos")
            petsAdapter.updatePetsChoose(photos)
        })


        galleryViewModel.currentSelectedPet.observe(viewLifecycleOwner){ petId ->

            //set the chosen image
            val petChosenImage = root.findViewById<ImageView>(R.id.gallery_pet_show)
            val petChoosenText = root.findViewById<TextView>(R.id.gallery_pet_name)

            if (petId == null) {
                // No pet selected, return to the initalized format
                petChosenImage.setImageResource(R.drawable.game_choose_nth_3)
                petChoosenText.text = "Please Select a Pet!"
                petChosenImage.setOnLongClickListener(null)
            } else {
                // Pet selected, display the image
                petChosenImage.setImageResource(petInfo.getPetInfoById(petId)?.imageId ?: R.layout.gallery_pet_items_frame)
                petChoosenText.text = petInfo.getPetInfoById(petId)?.name
                petChosenImage.visibility = View.VISIBLE

                //set a long click so it can show the info of the pet
                petChosenImage.setOnLongClickListener{
                    showPetDetailDialog(petId)
                    true
                }
            }

        }



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