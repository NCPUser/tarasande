package net.tarasandedevelopment.tarasande_protocol_hack.provider.viacursed

import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.provider.OnlineModeAuthProvider

class FabricOnlineModeAuthProvider : OnlineModeAuthProvider {

    init {
        System.setProperty("Bedrock-Key", "EwAYA%2bpvBAAUKods63Ys1fGlwiccIFJ%2bqE1hANsAAbVaDBBiqYCCxfBKR4fO%2b%2b7oU5z3JgZY8r4Ey%2bflL0TuWDVNnTAxV6rSPQMfj7EnRxPGK6zJTMUZ/Mzn7Rq7tyYpxuFGN4L9agjtbZ/xeFA8C869QtTMmclTHbJbOvp1GCxL04XO6Eq3lGbjoZgFyBCHn088dZk%2bglBVMJJbNf63nd1K5Emoj%2bA6QHWFOcVTzcreDn7E2oMp48FBzLtdgQE407nBX37L1aoaBzlunipzrun1w3lnk0Ezsrs1NEocbQodIqT8IAS%2bSMT3gqsbbRWNsy2pA/iu1lsRghU6kuQv4dgqqhqEGdw/XhKat7uA0awj%2bNNujBXdMAMcD8nEhKwDZgAACIsyGATIeEAq6AF4tYVTXDQ/W5zfH3G8GsZcvYSgLdIyyQK6P8gIE%2bIPZtu8c8E9e7W35wiaICsNDnd558hfTKCuFb46MOkUWcoiQ2yffkNAMWMei5MGpoHsuorpz%2b21hbO38un5gzNjhfA4EEg7SFgcRu9F3MWDrVDLStcejkktXe5Cuj80vUTffGAfn7Z5FRgVbi5vt5fjZPj65fNkdGS4p%2bTB3mnjyiZT4N%2bw0r/CHvUIAeZMNDnNXBYmXdC7JuZu%2bRU3dKGHQ1K5uQVlP4HtXscLkYj3IVOjbZxzPCCuQhAVbkI8VRK0z8SqAaNE1XPzTn7POqRcJS9tXfFWG5gkPtAjNEgsz5rbiHT8E0b%2bPx7rPfdWQ51LrhECbhf0/qvAikoAJWiPXW69sUYEz/xrYhtT17Uuvvfefhjb3ZY24C/%2bhKdSfIpqktdeqqPfOoJfCzBFiEGDlqTkfF3B3ElhCMJBc8PnnXCZC896FIXlZ5cnSPY560eo6CHVNTpe%2bU12fj5EmkXLhYAS6XfUBOTPUriFdtrAJ0CKKbaaqJmjD3d/ZzVAMmfMJ22gBMjs8kgODWwqWmhVc4H7yL85galSinHA1m/oBDM6%2bcTE4jcgqi5sKbSY6NmuUnj0BNZhWgD5G21p4Hn7Hp2P7QeX3K22iD8C&token_type=bearer&expires_in=86400&scope=service::user.auth.xboxlive.com::MBI_SSL&refresh_token=M.R3_BL2.-CfK1WIqWaXPT2tQj!otJfYrVAvhIPfoN80czRi7sxna8oXLUMrSu115ruI4HP7Ttk5f5rt9G7Av3QQ4ldReuwYK4LKsodmisNXQVwhGgZBPfK5T!1XfQ04zUXXHehXCXXA!2E6WWuXi7ZfhqZ71PkDjod2WgrqntecGu8zODo8yc4oT2jy6qf0EDy1*evnTDiaFcLXB2Eor6FVxaRlQODwSrRTAXyLwVq7Wyh2PDV1QorLYARO8H9c3qyHw3E60RYkJrtbvcKiEmLKsKMMMZxpgNq9G!CuLr!4YUlYLzIxFX")
    }

    override fun getAuthToken() = System.getProperty("Bedrock-Key")
}
