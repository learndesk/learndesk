import React from 'react'
import { resolve } from 'path'
import { render } from 'mjml-react'
import { sync as rimrafSync } from 'rimraf'
import { mkdirSync, writeFileSync } from 'fs'

import * as mails from './src'

const locales = [ 'fr', 'en' ]

const maildir = resolve(__dirname, '..', 'src', 'main', 'resources', 'email')
rimrafSync(maildir)
mkdirSync(maildir)

locales.forEach(locale => {
  mkdirSync(resolve(maildir, locale))

  Object.keys(mails).forEach(mail => {
    const Email = mails[mail]
    const { html, errors } = render(React.createElement(Email, { locale }))
    if (errors.length !== 0) {
      console.error(errors)
      process.exit(1)
    }

    writeFileSync(resolve(maildir, locale, `${mail}.html`), html)
  })
})
