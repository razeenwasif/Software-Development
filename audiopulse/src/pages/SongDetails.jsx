import { useParams } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import { DetailsHeader, Error, Loader, RelatedSongs } from "../components";

import { setActiveSong, playPause } from "../redux/features/playerSlice";
import { useGetSongDetailsQuery, useGetSongRelatedQuery } from "../redux/services/shazamCore";

const SongDetails = () => {
  const dispatch = useDispatch();
  const { songid, id: artistId } = useParams();
  const { activeSong, isPlaying } = useSelector((state) => state.player);
  
  const { data: songData, isFetching: isFetchingSongDetails } = useGetSongDetailsQuery({ songid });
  const { data, isFetching: isFetchingRelatedSongs, error } = useGetSongRelatedQuery({ songid });

  // These keep breaking the application
  // if (isFetchingSongDetails || isFetchingRelatedSongs) {
  //   return <Loader title="Searching song details"/>;
  // }

  // if (error) {
  //   return <Error message={"Something went wrong..."}/>
  // }

  const handlePauseClick = () => {
    dispatch(playPause(false));
  }

  const handlePlayClick = (song, i) => {
    dispatch(setActiveSong({ song, data, i }));
    dispatch(playPause(true));
  }

  return (
    <div className="flex flex-col">
      <DetailsHeader artistId={artistId} songData={songData} />

      <div className="mb-10">
        <h2 className="text-white text-3xl font-bold">Lyrics:</h2>
        <div className="mt-5">
          {songData?.sections[1].type === "LYRICS" ? (
            songData?.sections[1].text.map((line, i) => (
              <p
                className="text-base my-1"
                style={{
                  background: "linear-gradient(to right, #FC466B, #3F5EFB)",
                  "-webkit-background-clip": "text",
                  "-webkit-text-fill-color": "transparent",
                }}
              >
                {line}
              </p>
            ))
          ) : (
            <p className="text-white text-base my-1">
              Sorry, no lyrics found for this song.
            </p>
          )}
        </div>
      </div>

      <RelatedSongs 
        data={data}
        isPlaying={isPlaying}
        activeSong={activeSong}
        handlePauseClick={handlePauseClick}
        handlePlayClick={handlePlayClick}
      />
    </div>
  );
};

export default SongDetails;
