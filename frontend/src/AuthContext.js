// AuthContext.js
import React, { createContext, useContext, useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const AuthContext = createContext()

export function useAuth() {
  return useContext(AuthContext)
}

export async function signup(firstName, lastName, username, email, password, navigate){
    try {
        await axios
          .post(`${process.env.REACT_APP_API_URL}/api/auth/signup`, {
            firstName: firstName,
            lastName: lastName,
            username: username,
            email: email,
            password: password
          })
          .then(
            (res) => {
              // Registration successful
              alert(
                'Registration Successful. Please Login to your New Account!',
              )
              navigate('/login')
            },
            (error) => {
              const errorType = error.response.data
              if(errorType){
                alert(errorType)
              } else {
                // Other types of errors
                alert(
                  'Oops...an error occurred. Please make sure all of your entered information is correct and try again.',
                )
                console.error(error) // Log the error for debugging
              }
            },
          )
      } catch (err) {
        // Handle other errors
        alert('An unexpected error occurred. Please try again.')
      }
}

export async function checkIsServiceProviderLoggedIn() {
  try {
    const response = await axios.get('/api/account/type', {
      withCredentials: true,
    })
    const type = response.data
    console.log(type)
    return type === 'SERVICE_PROVIDER'
  } catch (error) {
    console.error(error)
    return null // or some other appropriate error handling
  }
}

export async function checkIsCustomerLoggedIn() {
  try {
    const response = await axios.get('/api/account/type', {
      withCredentials: true,
    })
    const type = response.data
    console.log(type)
    return type === 'CUSTOMER'
  } catch (error) {
    console.error(error)
    console.log(false)
    return false // or some other appropriate error handling
  }
}

export function logout() {
  axios.get('/api/logout', {
    withCredentials: true,
  })
  alert('You have now been Logged Out. ')
  // Clear any other user-related data as well
}