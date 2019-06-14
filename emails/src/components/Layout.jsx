import React from 'react'
import {
  Mjml,
  MjmlBody,
  MjmlColumn,
  MjmlDivider,
  MjmlImage,
  MjmlSection
} from 'mjml-react'
import Footer from './Footer'
import Header from './Header'

export default ({ locale, solicited, children }) =>
  <Mjml>
    <Header/>
    <MjmlBody>
      <MjmlSection>
        <MjmlColumn>
          <MjmlImage
            src="https://cdn.bowser65.xyz/learndesk/logo.png"
            href="https://learndesk.app"
            align="center"
            padding={0}
            width={400}
          />
          <MjmlDivider borderColor="#f49898" borderWidth={4} paddingTop={0}/>
        </MjmlColumn>
      </MjmlSection>
      {children}
      <Footer locale={locale} solicited={solicited}/>
    </MjmlBody>
  </Mjml>
