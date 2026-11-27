package com.works.app.utils


object LocationRepository {

    fun getLocations(): List<LocationModel> {

        return listOf(
            LocationModel(
                1,
                "İstanbul",
                41.0082,
                28.9784,
                "İstanbul şehir merkezi"
            ),
            LocationModel(
                2,
                "Ankara",
                39.9334,
                32.8597,
                "Ankara şehir merkezi"
            ),
            LocationModel(
                3,
                "İzmir",
                38.4237,
                27.1428,
                "İzmir şehir merkezi"
            ),
            LocationModel(
                4,
                "Bursa",
                40.1885,
                29.0610,
                "Bursa şehir merkezi"
            )
        )
    }
}
