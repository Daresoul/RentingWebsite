

export const DateToISO = (date: Date) => {
	const dateString = date.toLocaleDateString()

	const dateArray = dateString.split("/")

	return dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0] + "T00:00:00.000Z"

}