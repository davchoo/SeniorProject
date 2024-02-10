import React from 'react'
import { Autocomplete } from '@react-google-maps/api'


export const AutoComplete = () => {
 return (
   <div>
       <div className='flex flex-row'>
       <label>Enter Departue:</label>
       <Autocomplete >
         <input></input>
       </Autocomplete>
     </div>
     <div className='flex flex-row'>
       <label>Enter Destination:</label>
       <Autocomplete >
         <input></input>
       </Autocomplete>
     </div>
   </div>
 )
}
