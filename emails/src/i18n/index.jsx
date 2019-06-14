import React from 'react'
import { MjmlText } from 'mjml-react'

const locales = {
  en: require('./strings/en.json'),
  fr: require('./strings/fr.json')
}

export default (props) => {
  let string = locales[props.locale] || locales.en
  const stringPaths = props.string.split('.')
  for (const fragment of stringPaths) {
    string = string[fragment]
  }

  const newProps = { ...props, children: string }
  delete newProps.locale
  delete newProps.string
  return <MjmlText {...newProps}/>
}
