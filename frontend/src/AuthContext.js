// AuthContext.js
import React, { createContext, useContext, useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
import Navbar from './components/Navbar'

const AuthContext = createContext()

export function useAuth() {
  return useContext(AuthContext)
}

export async function signup(firstName, lastName, username, password, navigate){
    try {
        await axios
          .post(`${process.env.REACT_APP_API_URL}/api/auth/signup`, {
            firstName: firstName,
            lastName: lastName,
            username: username,
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

export async function login(username, password, navigate) {
    try {
      await axios
        .post(
            `${process.env.REACT_APP_API_URL}/api/auth/login`,
          {
            username: username,
            password: password,
          },
          { withCredentials: true },
        )
        .then((result) => {
          if (result) {
            window.location = "/"
        }})
        .catch((fail) => {
          console.error(fail) // Error!
          if (fail.response && fail.response.status == 403) {
            alert('Invalid credentials. Please try again.')
            return
          }
          alert('Oops...an error occurred. Please try again.')
        })
    } catch (err) {
      alert(err)
    }
  }


  export async function checkIsLoggedIn() {
    try {
      const response = await axios.get(
        `${process.env.REACT_APP_API_URL}/api/auth/check-login`,
        { withCredentials: true }
      );
      console.log(response.data);
      return response.data;
    } catch (error) {
      console.error(error);
      return false;
    }
  }
  

export function logout() {
  axios.get(`${process.env.REACT_APP_API_URL}/api/logout`, {
    withCredentials: true,
  })
  alert('You have now been Logged Out. ')
  // Clear any other user-related data as well
}