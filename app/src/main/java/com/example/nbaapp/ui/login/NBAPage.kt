package com.example.nbaapp.ui.login

import com.example.nbaapp.CustomAdapter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.nbaapp.R
import org.json.JSONArray


class NBAPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nbapage)
        supportActionBar?.show()

        val searchBar = findViewById<EditText>(R.id.editTextTextPersonName)
        val search = findViewById<Button>(R.id.searchButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.requestFocus()





        search.setOnClickListener {

            getStats()

        }










    }

    fun getStats() {
        val playerSearchUrl = "https://www.balldontlie.io/api/v1/players?search="
        val seasonAverageUrl = "https://www.balldontlie.io/api/v1/season_averages?season="
        val searchBar = findViewById<EditText>(R.id.editTextTextPersonName)

        var searchBarText = searchBar.text.toString()
        if (!searchBarText.contains(",")) {
            searchBarText = "$searchBarText,"
        }
        val searchArray = searchBarText.split(",").toMutableList()
        val query = searchArray[0].trim()
        var maxPage = 3
        var currPage = 1


        val dataPlayers = mutableListOf<PlayerStats>()

        for (x in 1 until maxPage) {

            val url = "$playerSearchUrl$query&page=$currPage"
            val playerRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    val dataOne = response.getJSONArray("data")
                    val metaObject = response.getJSONObject("meta")
                    maxPage = metaObject.getInt("total_pages") + 1

                    for (i in 0 until dataOne.length()) {
                        val player = dataOne.getJSONObject(i)
                        val playerID = player.getString("id").toString()

                        if (searchArray[1].isBlank()) {
                            searchArray[1] = "2022"
                        }
                        val year = searchArray[1].trim()
                        val seasonAvgReqUrl = "$seasonAverageUrl$year&player_ids[]=$playerID"

                        val seasonAvgRequest = JsonObjectRequest(
                            Request.Method.GET,
                            seasonAvgReqUrl,
                            null,
                            { response ->
                                val fName = player.getString("first_name")
                                val lName = player.getString("last_name")
                                val team = player?.getJSONObject("team")?.getString("name") ?: "--"
                                val heightFt =
                                    if (!player.isNull("height_feet")) player.getInt("height_feet")
                                        .toString() else "--"
                                val heightIn =
                                    if (!player.isNull("height_inches")) player.getInt("height_inches")
                                        .toString() else "--"
                                val height = "$heightFt'$heightIn\""
                                val weight =
                                    if (!player.isNull("weight_pounds")) player.getInt("weight_pounds")
                                        .toString() else "--"
                                val fullName = "$fName $lName"
                                val data = response.getJSONArray("data")
                                val playerAvg = data.optJSONObject(0)
                                val gamesPlayed = playerAvg?.getString("games_played") ?: "--"
                                val minPerGame = playerAvg?.getString("min") ?: "--"
                                val ptsPerGame = playerAvg?.getString("pts") ?: "--"
                                val astsPerGame = playerAvg?.getString("ast") ?: "--"
                                val rebPerGame = playerAvg?.getString("reb") ?: "--"
                                val stlPerGame = playerAvg?.getString("stl") ?: "--"
                                val blkPerGame = playerAvg?.getString("blk") ?: "--"
                                val fgPCT = playerAvg?.getString("fg_pct") ?: "--"
                                val fg3PCT = playerAvg?.getString("fg3_pct") ?: "--"
                                val FTPct = playerAvg?.getString("ft_pct") ?: "--"
                                val TOPerGame = playerAvg?.getString("turnover") ?: "--"
                                dataPlayers.add(
                                    PlayerStats(
                                        fullName,
                                        team,
                                        height,
                                        weight,
                                        gamesPlayed,
                                        minPerGame,
                                        ptsPerGame,
                                        astsPerGame,
                                        rebPerGame,
                                        stlPerGame,
                                        blkPerGame,
                                        fgPCT,
                                        fg3PCT,
                                        FTPct,
                                        TOPerGame
                                    )
                                )


                            },

                            { error ->

                            }


                        )
                        Volley.newRequestQueue(this).add(seasonAvgRequest)


                    }


                }, { error ->


                })
            Volley.newRequestQueue(this).add(playerRequest)

            currPage++
        }
        updateView(dataPlayers)
    }





    fun updateView(dataPlayers: MutableList<PlayerStats>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = CustomAdapter(dataPlayers)
        recyclerView.adapter = adapter
    }
}
















