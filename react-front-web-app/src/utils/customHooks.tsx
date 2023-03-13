import { useEffect, useRef, useState, MutableRefObject } from "react";

export function useHover<T extends HTMLElement = HTMLElement>(): [
  MutableRefObject<T>?,
  boolean?,
] {
  const [value, setValue] = useState<boolean>(false)
  const ref = useRef<T>(null)
  const handleMouseOver = () => setValue(true)
  const handleMouseOut = () => setValue(false)
  useEffect(
    // eslint-disable-next-line consistent-return
    () => {
      const node = ref.current
      if (node) {
        node.addEventListener('mouseover', handleMouseOver)
        node.addEventListener('mouseout', handleMouseOut)
        // removing listener while mouse is still on moving to avoid multi renders
        return () => {
          node.removeEventListener('mouseover', handleMouseOver)
          node.removeEventListener('mouseout', handleMouseOut)
        }
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [ref.current],
  );
  return [ref as MutableRefObject<T>, !!value]
}

export function useFocus(ref: any, mounted: boolean): boolean {

  const [state, setState] = useState(false);

  useEffect(() => {
    const node = ref.current
    const onFocus = () => setState(true);
    const onBlur = () => setState(false);
    if (node) {
      node.addEventListener('focus', onFocus);
      node.addEventListener('blur', onBlur);
    }

    return () => {
      if (node) {
        node.removeEventListener('focus', onFocus);
        node.removeEventListener('blur', onBlur);
      }
    };
  },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [ref.current]
  );
  return state;
}
