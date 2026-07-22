import { useState } from "react"

export default function SearchBarAndButton({
  searchText,
  setSearchText,
}) {

  return (
    <div className="flex justify-center">
       <input placeholder="Find Recipes by Name" value = {searchText} onChange={(e) => setSearchText(e.target.value)} name = "name"/>
       {/* <button>Search</button> */}
    </div>
  )
}