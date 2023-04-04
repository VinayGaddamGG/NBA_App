package com.example.nbaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nbaapp.ui.login.PlayerStats

class CustomAdapter(
    private val playerStatsList: MutableList<PlayerStats>,
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            val playerStats = playerStatsList[position]
            holder.bind(playerStats)


        // sets the text to the textview from our PlayerStats class




    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return playerStatsList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        private val playerName: TextView = itemView.findViewById(R.id.playerName)
        private val teamName: TextView = itemView.findViewById(R.id.teamName)
        private val height: TextView = itemView.findViewById(R.id.height)
        private val weight: TextView = itemView.findViewById(R.id.weight)
        private val games: TextView = itemView.findViewById(R.id.games)
        private val mins: TextView = itemView.findViewById(R.id.mins)
        private val ppg: TextView = itemView.findViewById(R.id.ppg)
        private val asts: TextView = itemView.findViewById(R.id.assts)
        private val rebounds: TextView = itemView.findViewById(R.id.rebounds)
        private val steals: TextView = itemView.findViewById(R.id.steals)
        private val blocks: TextView = itemView.findViewById(R.id.blocks)
        private val fgPCT: TextView = itemView.findViewById(R.id.fgPCT)
        private val fg3PCT: TextView = itemView.findViewById(R.id.fg3PCT)
        private val ftPCT: TextView = itemView.findViewById(R.id.ftPCT)
        private val TOPPG: TextView = itemView.findViewById(R.id.TOPPG)







        fun bind(playerStats: PlayerStats){


            playerName.text = playerStats.name
            teamName.text = playerStats.teamDisplay
            height.text = playerStats.heightDisplay
            weight.text = playerStats.weightDisplay + " lbs"
            games.text ="Games Played: " + playerStats.gameDisplay
            mins.text = "MPG: " + playerStats.minGameDisplay
            ppg.text = "PPG: " + playerStats.ppgDisplay
            asts.text = "APG : " + playerStats.asstsDisplay
            rebounds.text = "RPG: " + playerStats.rbsDisplay
            steals.text = "SPG: " + playerStats.stlDisplay
            blocks.text = "BPG: " + playerStats.blkDisplay
            fgPCT.text = "FG%: " + playerStats.fgPCTDisplay
            fg3PCT.text = "FG3%: " + playerStats.fg3PCTDisplay
            ftPCT.text = "FT%: " +  playerStats.FTPctDisplay
            TOPPG.text = "TOPPG: " + playerStats.TOPerGameDisplay





        }
    }
}


